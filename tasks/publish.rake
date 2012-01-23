# Tasks for preparing a publication package for the NCI's Nexus repo.

namespace :publish do
  PROJECT_PUBLICATION_LOCAL_ROOT = File.expand_path("../../nexus-staging", __FILE__)

  task :version_check do
    projects.each do |p|
      unless p.version =~ /^\d+\.\d+.\d+\.RELEASE$/ || ENV['TEST_RELEASE']
        fail "#{p} has a non-release version.\nPlease set it to x.y.z.RELEASE (according to the policy in the README) and commit before attempting to release."
      end
    end
  end

  def which_vcs
    @which_vcs ||= begin
                     p = project('ctms-commons')
                     if File.directory?(p._('.git'))
                       :git
                     elsif File.directory?(p._('.svn'))
                       :svn
                     end
                   end
  end

  task :vcs_check do
    case
    when ENV['TEST_RELEASE']
      # pass
    when which_vcs == :git
      unless `git status -s`.empty?
        fail "Outstanding changes in the working directory.  Please resolve them before releasing."
      end
    when which_vcs == :svn
      unless `svn status`.empty?
        fail "Outstanding changes in the working directory.  Please resolve them before releasing."
      end
    end
  end

  desc "Ensure that the project is ready to publish"
  task :check => [:version_check, :vcs_check] do
    info "Everything seems to be ready to publish."
  end

  desc "Tag a release version in the project subversion repo"
  task :tag => :check do
    unless which_vcs == :svn
      fail "Tagging may only be done from an svn checkout."
    end
    system("svn cp ^/trunk ^/tags/releases/#{CTMS_COMMONS_VERSION} -m 'Tag #{CTMS_COMMONS_VERSION}'")
    fail "Tagging failed" unless $? == 0
  end

  task :build => ["rake:clean", "rake:package"]

  directory PROJECT_PUBLICATION_LOCAL_ROOT

  task :clean => PROJECT_PUBLICATION_LOCAL_ROOT do |t|
    rm_rf Dir[File.join(PROJECT_PUBLICATION_LOCAL_ROOT, '*')].sort_by { |fn| fn.size }
  end

  task :copy => [PROJECT_PUBLICATION_LOCAL_ROOT, :clean] do
    project_repo = ProjectIvyRepo::PROJECT_REPO_ROOT
    prefix = File.join(project_repo, CTMS_COMMONS_IVY_ORG) + "/"
    jars = Dir[File.join(prefix, "**/*.jar")]

    mkdir_p PROJECT_PUBLICATION_LOCAL_ROOT
    jars.each do |jar|
      target_jar = File.join(PROJECT_PUBLICATION_LOCAL_ROOT, File.basename(jar))
      target_pom = target_jar.sub(/jar$/, 'pom')
      cp jar, target_jar
      cp File.join(File.dirname(jar), 'pom.xml'), target_pom
    end

    info "Copied #{jars.size * 2} artifacts to the publish staging directory."
  end

  desc "Does a sanity check on the prepared artifacts"
  task :sanity => PROJECT_PUBLICATION_LOCAL_ROOT do
    problems = prepared_artifacts.collect { |st, path|
      if path =~ /ivy.xml$/ || path =~ /pom.xml$/
        if File.read(path) =~ /\.DEV/
          "#{path} contains a dependency on a development artifact."
        end
      end
    }.compact
    unless problems.empty?
      msg = "There are problems with the soon-to-be-published artifacts.  " <<
        "Please fix them before publishing.\n- #{problems.join("\n- ")}"
      if ENV['TEST_RELEASE']
        puts msg
        puts 'Continuing because this is a test run'
      else
        fail msg
      end
    end
  end

  desc "Prepare the project artifacts for publication"
  task :prepare => [:check, :build, :copy, :index, :sanity] do
    info "#{PROJECT_PUBLICATION_LOCAL_ROOT} now contains the artifacts for #{CTMS_COMMONS_VERSION}."
    info "Please verify they are correct, then run `buildr publish:zip`."
  end

  desc 'Create the index spreadsheet required by NCICB'
  task :index do
    fn = File.join(PROJECT_PUBLICATION_LOCAL_ROOT, "artifacts-#{CTMS_COMMONS_VERSION}.csv")
    cd PROJECT_PUBLICATION_LOCAL_ROOT do
      File.open(fn, 'w') do |f|
        projects.each do |p|
          gav = [CTMS_COMMONS_IVY_ORG, p.id, p.version].join('/')
          artifact = "#{p.id}-#{p.version}.jar"
          next unless File.exist? artifact
          f.puts [
            artifact,
            Digest::SHA1.hexdigest(File.read(artifact)),
            gav,
            'jar',
            'yes'
          ].join(',')
        end
      end
    end
  end

  task :zip do
    fn = projects.first.path_to(
      :target, "ctms-commons-#{projects.first.version}-nexus-artifacts.zip")
    mkdir_p File.dirname(fn)
    rm_rf fn
    cd PROJECT_PUBLICATION_LOCAL_ROOT do
      Dir['**/*'].each do |artifact|
        Zip::ZipFile.open(fn, Zip::ZipFile::CREATE) do |zf|
          zf.add(artifact, artifact)
        end
      end
    end
    puts "The release artifacts are packaged in #{fn}."
  end

  def prepared_artifacts
    Dir[File.join(PROJECT_PUBLICATION_LOCAL_ROOT, '**', '*')]
  end
end
