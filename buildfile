require 'buildr_iidea'

CTMS_COMMONS_VERSION = "1.0.0.DEV"

desc "Shared libraries for caBIG CTMS projects"
define "ctms-commons" do
  project.version = CTMS_COMMONS_VERSION
  project.group = "gov.nih.nci.cabig.ctms"

  desc "Zero-dependency common code for all other packages"
  define "base" do
    package(:jar)
  end

  define "lang" do
    compile.with project('base'),
      SLF4J.jcl, JAKARTA_COMMONS.lang, GENERIC_COLLECTIONS
    package(:jar)
  end

  define "testing-all" do
    define "testing" do
      compile.with JUNIT, EASYMOCK, project("lang"), SLF4J.jcl
      package(:jar)
    end

    # define "uctrace"
  end
  
  define "core" do
    compile.with project("base").and_dependencies, project('lang').and_dependencies,
      HIBERNATE, SLF4J.api, SPRING.main, ANT
    test.with EASYMOCK, JAKARTA_COMMONS.collections, SLF4J.simple, project("testing-all:testing"), HSQLDB
    package(:jar)
  end

  define "laf" do
    # TODO: deploy and run the demo, if anyone's still using it
    compile.with SERVLET, JAKARTA_COMMONS.io, project("web").and_dependencies
    test.with SPRING.test, SLF4J.simple
    package(:jar)
  end

  define "web" do
    compile.with project("core").and_dependencies, SERVLET, SLF4J.jcl,
      SPRING.main, SPRING.webmvc, SITEMESH
    test.with SPRING.test, project("testing-all:testing").and_dependencies, SLF4J.simple
    package(:jar)
  end
end
