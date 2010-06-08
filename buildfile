require 'buildr_iidea'
require 'buildr_bnd'
repositories.remote.concat Buildr::Bnd.remote_repositories
require 'buildr/ivy_extension'

CTMS_COMMONS_VERSION = "1.0.0.DEV"

desc "Shared libraries for caBIG CTMS projects"
define "ctms-commons" do
  project.version = CTMS_COMMONS_VERSION
  project.group = "gov.nih.nci.cabig.ctms"

  desc "Zero-dependency common code for all other packages"
  define "base" do
    ivy.compile_conf('compile').test_conf('unit-test')
    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "lang" do
    ivy.compile_conf('compile').test_conf('unit-test')
    interproject_dependencies << 'base'

    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "testing" do
    define "unit" do
      ivy.compile_conf('compile').test_conf('unit-test')
      interproject_dependencies << 'lang'
      package(:jar)
    end

    define "uctrace" do
      ivy.compile_conf('compile').test_conf('unit-test')
      interproject_dependencies << 'base'
      package(:jar)
    end
  end

  define "core" do
    ivy.compile_conf('compile').test_conf('unit-test')
    interproject_dependencies << 'base' << 'lang' << 'testing:unit'

    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "laf" do
    # TODO: deploy and run the demo, if anyone's still using it
    ivy.compile_conf('compile').test_conf('unit-test')
    interproject_dependencies << 'web'

    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "web" do
    ivy.compile_conf('compile').test_conf('unit-test')
    interproject_dependencies << 'base' << 'lang' << 'core' << 'testing:unit'

    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "acegi" do
    # TODO: this probably should be something ctms-specific
    project.group = "gov.nih.nci.security.acegi"

    define "acl-dao", :base_dir => _('acegi-acl-dao') do
      compile.with ACEGI, HIBERNATE, SPRING.main, EHCACHE, SLF4J.jcl
      test.with SPRING.test, SLF4J.api, SLF4J.simple, CGLIB, HSQLDB,
        JAKARTA_COMMONS.dbcp, JAKARTA_COMMONS.pool, JAKARTA_COMMONS.collections
      package(:jar)
    end

    define "csm", :base_dir => _('acegi-csm') do
      compile.with SLF4J.jcl, CSM, ASPECTJ, SPRING.main, ACEGI, SERVLET, HIBERNATE
      test.with EASYMOCK, SLF4J.simple, SLF4J.api
      package(:bundle).tap do |bundle|
        bundle["Export-Package"] = bnd_export_package
      end
    end

    define "csm-test", :base_dir => _('acegi-csm-test') do
      compile.with JUNIT, DBUNIT, SPRING.main, HIBERNATE
      test.with project("csm").and_dependencies, SLF4J.api, SLF4J.simple, HSQLDB,
        JAKARTA_COMMONS.collections, SLF4J.log4j
    end

    define "grid", :base_dir => _('acegi-grid') do
      compile.with SLF4J.jcl, project('csm').and_dependencies, GLOBUS, CAGRID
      test.with SLF4J.api, SLF4J.simple
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
