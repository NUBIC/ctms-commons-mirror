package gov.nih.nci.cabig.ctms.maven.uctrace;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.resolver.ArtifactCollector;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilder;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;
import org.apache.maven.shared.dependency.tree.DependencyTree;
import org.apache.maven.shared.dependency.tree.DependencyNode;
import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author Rhett Sutphin
 * @goal trace
 * @aggregator true
 * @requiresDependencyResolution
 */
public class UseCaseTraceMavenMojo extends AbstractMojo {
    // This needs to be kept in sync with the actual artifact id for this plugin
    private static final String ARTIFACT_ID = "uctrace-maven-plugin";

    /**
     * The path to the apt executable
     *
     * @parameter default-value="${java.home}/bin/apt"
     */
    private String aptBin;

    /**
     * @parameter default-value="${reactorProjects}"
     */
    private List<MavenProject> reactorProjects;

    /**
     * The desired outputDirectory
     *
     * @parameter default-value="${basedir}/target"
     */
    private File outputDirectory;

    /**
     * The annotation class which accumulates {@link UseCase} values for
     * each test case.
     *
     * @parameter
     * @required
     */
    private String useCasesAnnotationClassName;

    /**
     * @parameter default-value="${project.pluginArtifacts}
     */
    private Set<Artifact> projectPlugins;

    /**
     * @parameter default-value="${localRepository}"
     */
    private ArtifactRepository artifactRepository;

    /**
     * @component
     */
    private ArtifactFactory artifactFactory;

    /**
     * @component
     */
    private ArtifactMetadataSource artifactMetadataSource;

    /**
     * @component
     */
    private ArtifactCollector artifactCollector;

    /**
     * @component
     */
    private DependencyTreeBuilder treeBuilder;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File testCaseList, classpath;
        try {
            testCaseList = buildTestCasesListFile();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to build test cases list", e);
        }
        try {
            classpath = buildClasspathFile();
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to build test cases list", e);
        }

        String[] cmd =  {
            aptBin,
            "-nocompile",
            "-factory", UseCaseTraceabilityAnnotationProcessorFactory.class.getName(),
            "-d", outputDirectory.getAbsolutePath(),
            UseCaseTraceabilityAnnotationProcessorFactory.USE_CASES_ANNOTATION_CLASS_NAME_OPT
                + useCasesAnnotationClassName,
            '@' + classpath.getAbsolutePath(),
            '@' + testCaseList.getAbsolutePath()
        };

        try {
            getLog().info("Executing apt as " + Arrays.asList(cmd));
            Process process = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            IOUtils.copy(process.getInputStream(), System.out);
            process.waitFor();
            if (process.exitValue() != 0) {
                throw new MojoFailureException("apt failed (returned non-zero exit code)");
            }
        } catch (IOException e) {
            throw new MojoExecutionException("IO problem while executing apt", e);
        } catch (InterruptedException e) {
            throw new MojoExecutionException("Interrupted while waiting for apt", e);
        }

        if (!getLog().isDebugEnabled()) {
            testCaseList.delete();
            classpath.delete();
        } else {
            getLog().debug("Retained test case list for debugging: " + testCaseList);
            getLog().debug("Retained classpath for debugging: " + classpath);
        }
    }

    private File buildTestCasesListFile() throws IOException {
        File listFile = File.createTempFile("uctrace-tclist-", "");
        FileWriter fw = new FileWriter(listFile);
        getLog().debug("Writing test cases list to " + listFile);
        for (MavenProject project : reactorProjects) {
            getLog().debug("Looking for tests in " + project.getArtifactId());
            for (Object rootO : project.getTestCompileSourceRoots()) {
                File root = new File((String) rootO);
                for (File f : findJava(root, new LinkedList<File>())) {
                    fw.write(f.getAbsolutePath());
                    fw.write("\n");
                }
            }
        }
        fw.close();
        getLog().debug("Finished writing test cases list");
        return listFile;
    }

    private List<File> findJava(File root, List<File> accumulator) {
        if (!root.exists()) {
            getLog().debug(" " + root + " does not exist");
            return accumulator;
        }
        getLog().debug(" recursing into " + root);
        for (File file : root.listFiles()) {
            if (file.getName().startsWith(".")) {
                // skip hidden files
            } else if (file.isDirectory()) {
                findJava(file, accumulator);
            } else {
                if (file.getName().endsWith(".java")) {
                    accumulator.add(file);
                }
            }
        }
        return accumulator;
    }

    private File buildClasspathFile() throws IOException, MojoExecutionException {
        File cpFile = File.createTempFile("uctrace-classpath-", "");
        FileWriter fw = new FileWriter(cpFile);
        getLog().debug("Writing classpath to " + cpFile);
        fw.write("-cp\n");
        fw.write(StringUtils.join(buildClasspath().iterator(), File.pathSeparator));
        fw.close();
        getLog().debug("Finished writing test cases list");
        return cpFile;
    }

    @SuppressWarnings({ "unchecked" })
    private Set<String> buildClasspath() throws MojoExecutionException {
        try {
            Set<String> cp = new LinkedHashSet<String>();
            addPluginClasspath(cp);
            for (MavenProject project : reactorProjects) {
                getLog().debug("Building classpath from " + project.getArtifactId());
                cp.addAll(project.getCompileClasspathElements());
                cp.addAll(project.getTestClasspathElements());

                DependencyTree tree = treeBuilder.buildDependencyTree(project, artifactRepository,
                    artifactFactory, artifactMetadataSource, artifactCollector);
                addDeps(cp, tree.getRootNode());
            }
            return cp;
        } catch (DependencyResolutionRequiredException e) {
            throw new MojoExecutionException("Failed to build classpath for apt", e);
        } catch (DependencyTreeBuilderException e) {
            throw new MojoExecutionException("Failed to build dep tree", e);
        }
    }

    @SuppressWarnings({ "unchecked" })
    private void addPluginClasspath(Set<String> cp) throws MojoExecutionException {
        // find the artifact for this plugin
        Artifact thisPlugin = null;
        for (Artifact artifact : projectPlugins) {
            if (artifact.getArtifactId().equals(ARTIFACT_ID)) {
                thisPlugin = artifact; break;
            }
        }
        if (thisPlugin == null) {
            throw new MojoExecutionException("Could not find the artifact "
                + ARTIFACT_ID + " in " + projectPlugins);
        }
        cp.add(artifactPath(thisPlugin));
    }

    private void addDeps(Set<String> cp, DependencyNode node) {
        cp.add(artifactPath(node.getArtifact()));
        for (Object child : node.getChildren()) {
            addDeps(cp, (DependencyNode) child);
        }
    }

    private String artifactPath(Artifact artifact) {
        StringBuilder path = new StringBuilder(artifactRepository.getBasedir());
        if (path.charAt(path.length() - 1) != File.separatorChar) {
            path.append(File.separatorChar);
        }
        return path.append(artifactRepository.pathOf(artifact)).toString();
    }

    public void setAptBin(String aptBin) {
        this.aptBin = aptBin;
    }

    public void setReactorProjects(List<MavenProject> reactorProjects) {
        this.reactorProjects = reactorProjects;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public void setUseCasesAnnotationClassName(String useCasesAnnotationClassName) {
        this.useCasesAnnotationClassName = useCasesAnnotationClassName;
    }

    public void setArtifactRepository(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    public void setArtifactFactory(ArtifactFactory artifactFactory) {
        this.artifactFactory = artifactFactory;
    }

    public void setArtifactMetadataSource(ArtifactMetadataSource artifactMetadataSource) {
        this.artifactMetadataSource = artifactMetadataSource;
    }

    public void setArtifactCollector(ArtifactCollector artifactCollector) {
        this.artifactCollector = artifactCollector;
    }

    public void setTreeBuilder(DependencyTreeBuilder treeBuilder) {
        this.treeBuilder = treeBuilder;
    }
}
