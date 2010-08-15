require 'buildr_iidea'
require 'buildr_bnd'
repositories.remote << Buildr::Bnd.remote_repository
require 'buildr/ivy_extension'

CTMS_COMMONS_VERSION = "1.0.6.DEV"
CTMS_COMMONS_IVY_ORG = "gov.nih.nci.cabig.ctms"

# All modules use the same ivy4r config
def configure_ivy(ivy_config)
  ivy.compile_conf('compile').
    compile_type('jar').
    test_conf('unit-test').
    test_type('jar')
end

desc "Shared libraries for caBIG CTMS projects"
define "ctms-commons" do
  project.version = CTMS_COMMONS_VERSION
  project.group = CTMS_COMMONS_IVY_ORG
  project.compile.options.target = "1.5"
  project.compile.options.source = "1.5"
  project.iml.excluded_directories << IVY_HOME
  project.iml.group = true

  ipr.add_component("CompilerConfiguration") do |component|
    component.option :name => 'DEFAULT_COMPILER', :value => 'Javac'
    component.option :name => 'DEPLOY_AFTER_MAKE', :value => '0'
    component.resourceExtensions do |xml|
      xml.entry :name => '.+\.nonexistent'
    end
    component.wildcardResourceExtensions do |xml|
      xml.entry :name => '?*.nonexistent'
    end
  end

  desc "Zero-dependency common code for all other packages"
  define "base" do
    configure_ivy(ivy)
    package(:bundle).tap do |bundle|
      configure_bundle(bundle)
    end
  end

  define "lang" do
    configure_ivy(ivy)
    interproject_dependencies << 'base'

    package(:bundle).tap do |bundle|
      configure_bundle(bundle)
    end
  end

  define "testing" do
    project.no_iml

    define "unit" do
      project.iml.group = true
      configure_ivy(ivy)
      interproject_dependencies << 'ctms-commons:lang'
      package(:jar)
    end

    define "uctrace" do
      project.iml.group = true
      configure_ivy(ivy)
      interproject_dependencies << 'ctms-commons:base'
      package(:jar)
    end
  end

  define "core" do
    configure_ivy(ivy)
    interproject_dependencies << 'base' << 'lang' << 'testing:unit'

    package(:bundle).tap do |bundle|
      configure_bundle(bundle)
    end
  end

  define "laf" do
    # TODO: deploy and run the demo, if anyone's still using it
    configure_ivy(ivy)
    interproject_dependencies << 'web'

    package(:bundle).tap do |bundle|
      configure_bundle(bundle)
    end
  end

  define "web" do
    configure_ivy(ivy)
    interproject_dependencies << 'base' << 'lang' << 'core' << 'testing:unit'

    package(:bundle).tap do |bundle|
      configure_bundle(bundle)
    end
  end

  define "acegi" do
    project.no_iml

    define "acl-dao", :base_dir => _('acegi-acl-dao') do
      project.iml.group = true
      configure_ivy(ivy)
      package(:jar)
    end

    define "csm", :base_dir => _('acegi-csm') do
      project.iml.group = true
      configure_ivy(ivy)

      package(:bundle).tap do |bundle|
        configure_bundle(bundle)
      end
    end

    define "csm-test", :base_dir => _('acegi-csm-test') do
      project.iml.group = true
      configure_ivy(ivy)
      interproject_dependencies << project.parent.project('csm')
    end

    define "grid", :base_dir => _('acegi-grid') do
      project.iml.group = true
      configure_ivy(ivy)
      interproject_dependencies << project.parent.project('csm')

      package(:bundle).tap do |bundle|
        configure_bundle(bundle)
      end
    end
  end

  define "suite" do
    project.no_iml
    project.version = "0.5.0.DEV"

    define "authorization" do
      project.iml.group = true
      configure_ivy(ivy)
      interproject_dependencies << 'ctms-commons:core' << 'ctms-commons:base'

      test.resources.filter.using( csm_db_properties(project.parent.parent) )

      package(:bundle).tap do |bundle|
        configure_bundle(bundle)
      end

      task "test:wipe_db" => ["#{project.name}:test:compile", "#{project.name}:testdeps"] do
        p = csm_db_properties(project.parent.parent)
        %w(purge schema seeddata).each do |n|
          info "Executing #{n}.sql on #{p['csm_db.db_type']}"
          ant('wipe_db').sql(
            :src => _(:target, :test, :resources, "csm-sql", p["csm_db.db_type"], "#{n}.sql"),
            :delimiter => '/',
            :delimitertype => 'row',
            :userid   => p['csm_db.username'],
            :password => p['csm_db.password'],
            :driver   => p['csm_db.driver'],
            :url      => p['csm_db.url'],
            :classpath => project.test.compile.dependencies.collect { |a| a.to_s }.join(';'),
            :keepformat => false
            )
        end
      end

      # TODO: this should work
      # check package(:bundle), "includes resources" do
      #  it.should contain('**/*.properties')
      # end
    end
  end

  doc_projects = projects(%w(base lang core web suite:authorization))
  javadoc.from(doc_projects)
  task :all_javadocdeps => doc_projects.collect { |p| p.task(:javadocdeps) } do
    doc_projects.each do |p|
      confs = [p.ivy.test_conf, p.ivy.compile_conf].flatten.uniq
      if deps = p.ivy.deps(confs)
        project.javadoc.with deps
        info "Ivy adding javadoc dependencies from #{p} '#{confs.join(', ')}' to project '#{project.name}'"
      end
    end
  end
  project.task :javadoc => :all_javadocdeps

  # The following submodules exist but are not part of the
  # whole-project build-release process.  This should be fixed at some
  # point.
  #
  # * ccts-websso-ui
  # * grid
  # * acegi/acegi-csm-testapp
end
