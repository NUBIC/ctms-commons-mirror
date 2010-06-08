require 'buildr_iidea'
require 'buildr_bnd'
repositories.remote.concat Buildr::Bnd.remote_repositories
require 'buildr/ivy_extension'

CTMS_COMMONS_VERSION = "1.0.0.DEV"
CTMS_COMMONS_IVY_ORG = "gov.nih.nci.cabig.ctms"

desc "Shared libraries for caBIG CTMS projects"
define "ctms-commons" do
  project.version = CTMS_COMMONS_VERSION
  project.group = CTMS_COMMONS_IVY_ORG
  project.iml.excluded_directories << IVY_HOME

  desc "Zero-dependency common code for all other packages"
  define "base" do
    ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "lang" do
    ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
    interproject_dependencies << 'base'

    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "testing" do
    project.no_iml

    define "unit" do
      ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
      interproject_dependencies << 'ctms-commons:lang'
      package(:jar)
    end

    define "uctrace" do
      ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
      interproject_dependencies << 'ctms-commons:base'
      package(:jar)
    end
  end

  define "core" do
    ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
    interproject_dependencies << 'base' << 'lang' << 'testing:unit'

    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "laf" do
    # TODO: deploy and run the demo, if anyone's still using it
    ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
    interproject_dependencies << 'web'

    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "web" do
    ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
    interproject_dependencies << 'base' << 'lang' << 'core' << 'testing:unit'

    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "acegi" do
    project.no_iml

    define "acl-dao", :base_dir => _('acegi-acl-dao') do
      ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
      package(:jar)
    end

    define "csm", :base_dir => _('acegi-csm') do
      ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')

      package(:bundle).tap do |bundle|
        bundle["Export-Package"] = bnd_export_package
      end
    end

    define "csm-test", :base_dir => _('acegi-csm-test') do
      ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
      interproject_dependencies << 'acegi:csm'
    end

    define "grid", :base_dir => _('acegi-grid') do
      ivy.compile_conf('compile').compile_type('jar').test_conf('unit-test').test_type('jar')
      interproject_dependencies << 'acegi:csm'
      package(:jar)
    end
  end

  # The following submodules exist but are not part of the
  # whole-project build-release process.  This should be fixed at some
  # point.
  #
  # * ccts-websso-ui
  # * grid
  # * acegi/acegi-csm-testapp
end
