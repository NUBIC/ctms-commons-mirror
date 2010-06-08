require 'buildr_iidea'
require 'buildr_bnd'
repositories.remote.concat Buildr::Bnd.remote_repositories

CTMS_COMMONS_VERSION = "1.0.0.DEV"

desc "Shared libraries for caBIG CTMS projects"
define "ctms-commons" do
  project.version = CTMS_COMMONS_VERSION
  project.group = "gov.nih.nci.cabig.ctms"

  desc "Zero-dependency common code for all other packages"
  define "base" do
    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "lang" do
    compile.with project('base'),
      SLF4J.jcl, JAKARTA_COMMONS.lang, GENERIC_COLLECTIONS
    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "testing-all" do
    define "testing" do
      compile.with JUNIT, EASYMOCK, project("lang"), SLF4J.jcl
      package(:jar)
    end

    define "uctrace" do
      compile.with project('base').and_dependencies
      package(:jar)
    end
  end

  define "core" do
    compile.with project("base").and_dependencies, project('lang').and_dependencies,
      HIBERNATE, SLF4J.api, SPRING.main, ANT
    test.with EASYMOCK, JAKARTA_COMMONS.collections, SLF4J.simple, project("testing-all:testing"), HSQLDB
    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "laf" do
    # TODO: deploy and run the demo, if anyone's still using it
    compile.with SERVLET, JAKARTA_COMMONS.io, project("web").and_dependencies
    test.with SPRING.test, SLF4J.simple
    package(:bundle).tap do |bundle|
      bundle["Export-Package"] = bnd_export_package
    end
  end

  define "web" do
    compile.with project("core").and_dependencies, SERVLET, SLF4J.jcl,
      SPRING.main, SPRING.webmvc, SITEMESH
    test.with SPRING.test, project("testing-all:testing").and_dependencies, SLF4J.simple
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
