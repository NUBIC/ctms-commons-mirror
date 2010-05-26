# main m2 repo
repositories.remote << "http://repo1.maven.org/maven2"
# SpringSource osgi-ified bundle repos
repositories.remote << "http://repository.springsource.com/maven/bundles/release"
repositories.remote << "http://repository.springsource.com/maven/bundles/external"

# Only list versions which appear in more than one artifact here
CORE_COMMONS_VERSION = "77"
SPRING_VERSION = "2.5.6"
SLF4J_VERSION = "1.5.11"

GENERIC_COLLECTIONS = "net.sourceforge.collections:collections-generic:jar:4.01"

def ivy_artifact(repo_url, org, mod, rev, art, filename=nil)
  filename ||= "#{art}-#{rev}.jar"
  url = URI.join(repo_url, "#{org}/#{mod}/#{rev}/#{filename}").to_s
  artifact_spec = "#{org}.#{mod}:#{art}:jar:#{rev}"
  download(artifact(artifact_spec) => url)
end

def cbiit_ivy(organization, mod, rev, art)
  ivy_artifact("https://ncisvn.nci.nih.gov/svn/cbiit-ivy-repo/trunk/",
    organization, mod, rev, art)
end

def cagrid_ivy(organization, mod, rev, art, filename=nil)
  ivy_artifact("http://software.cagrid.org/repository-1.2/",
    organization, mod, rev, art, filename)
end

def cagrid_artifact_group(mod, rev, art_prefix, suffixes)
  suffixes.inject({}) { |h, a|
    art = a.nil? ? art_prefix : "#{art_prefix}-#{a}"
    key = art.gsub('-', '_').to_sym
    h.merge!(key => cagrid_ivy("caGrid", mod, rev, art))
  }
end

def spring_osgi_apache_commons(name, version)
  "org.apache.commons:com.springsource.org.apache.commons.#{name}:jar:#{version}"
end

SLF4J = struct(
  :api    => "org.slf4j:slf4j-api:jar:#{SLF4J_VERSION}",
  :jcl    => "org.slf4j:jcl-over-slf4j:jar:#{SLF4J_VERSION}",
  :simple => "org.slf4j:slf4j-simple:jar:#{SLF4J_VERSION}",
  :log4j  => "org.slf4j:log4j-over-slf4j:jar:#{SLF4J_VERSION}"
  # :jul    => "org.slf4j:jul-to-slf4j:jar:#{SLF4J_VERSION}"
  )

JAKARTA_COMMONS = struct(
  :collections => spring_osgi_apache_commons("collections", "3.2.0"),
  :dbcp        => spring_osgi_apache_commons("dbcp", "1.2.2.osgi"),
  :io          => spring_osgi_apache_commons("io", "1.4.0"),
  :lang        => spring_osgi_apache_commons("lang", "2.4.0"),
  :pool        => spring_osgi_apache_commons("pool", "1.4.0")
)

HIBERNATE = struct(
  :main               => "org.hibernate:com.springsource.org.hibernate:jar:3.3.1.GA",
  :annotations        => "org.hibernate:com.springsource.org.hibernate.annotations:jar:3.4.0.GA",
  :annotations_common => "org.hibernate:com.springsource.org.hibernate.annotations.common:jar:3.3.0.ga",
  :antlr              => "org.antlr:com.springsource.antlr:jar:2.7.7",
  :javax_persistence  => "javax.persistence:com.springsource.javax.persistence:jar:1.0.0",
  :javax_transaction  => "javax.transaction:com.springsource.javax.transaction:jar:1.1.0",
  :dom4j              => "org.dom4j:com.springsource.org.dom4j:jar:1.6.1",
  :javassist          => "org.jboss.javassist:com.springsource.javassist:jar:3.3.0.ga"
  )

SPRING = struct(
  :main    => "org.springframework:spring:jar:#{SPRING_VERSION}",
  :test    => "org.springframework:spring-test:jar:#{SPRING_VERSION}",
  :webmvc  => "org.springframework:spring-webmvc:jar:#{SPRING_VERSION}"
  )

ANT = struct(
  :main => "org.apache.ant:ant:jar:1.7.1"
  )

CORE_COMMONS = struct(
  :main    => "edu.northwestern.bioinformatics:core-commons:jar:#{CORE_COMMONS_VERSION}",
  :testing => "edu.northwestern.bioinformatics:core-commons-testing:jar:#{CORE_COMMONS_VERSION}"
  )

###### WEB

SERVLET = struct(
  :api => "javax.servlet:servlet-api:jar:2.4",
  :jsp => "javax.servlet:jsp-api:jar:2.0"
  )

SITEMESH = [
  "opensymphony:sitemesh:jar:2.2.1"
  ]

###### CSM & SECURITY

ACEGI = struct(
  :main => "org.acegisecurity:acegi-security:jar:1.0.3"
  )

ASPECTJ = struct(
  :rt     => "org.aspectj:aspectjrt:jar:1.6.2",
  :weaver => "org.aspectj:aspectjweaver:jar:1.6.2"
  )

EHCACHE = struct(
  :ehcache => "net.sourceforge.ehcache:com.springsource.net.sf.ehcache:jar:1.5.0",
  :jsr107 => "net.sourceforge.jsr107cache:com.springsource.net.sf.jsr107cache:jar:1.0.0",
  :backport => "edu.emory.mathcs.backport:com.springsource.edu.emory.mathcs.backport:jar:3.0.0"
  )

CSM = struct(
  :api => cbiit_ivy("gov.nih.nci.security", "csm", "3.2", "csmapi"),
  :clm => cbiit_ivy("gov.nih.nci.security", "clm", "4.0", "clm"),
  :jdom => "jdom:jdom:jar:1.0"
  )

GLOBUS = struct(
  :cog_jglobus => cbiit_ivy("globus", "jcog", "4.0.3", "cog-jglobus"),
  :axis => cbiit_ivy("globus", "axis", "1.2RC2", "axis"),
  :addressing => cbiit_ivy("globus", "ws-addressing", "1.0", "addressing"),
  :wsrf_core_stubs => cbiit_ivy("globus", "ws-core-client", "4.0.3", "wsrf_core_stubs"),
  :bouncycastle => cbiit_ivy("bouncycastle", "bouncycastle", "jdk1.3_1.25", "jce"),
  :puretls => cbiit_ivy("globus", "puretls", "0.9b5", "puretls"),
  :cryptix => cbiit_ivy("globus", "cryptix32", "3.2", "cryptix32"),
  :cryptix_asn1 => cbiit_ivy("globus", "cryptix-asn1", "0.1.11", "cryptix-asn1")
  )

CAGRID = struct(
  cagrid_artifact_group(
    "authentication-service", "1.2", "caGrid-authentication-service", %w(client stubs)).
  merge!(cagrid_artifact_group("core", "1.2", "caGrid-core", [nil])).
  merge!(cagrid_artifact_group("dorian", "1.2", "caGrid-dorian", %w(stubs client))).
  merge!(cagrid_artifact_group("gridca", "1.2", "caGrid-gridca", [nil])).
  merge!(cagrid_artifact_group("authz", "1.2", "caGrid-authz", %w(common))).
  merge!(cagrid_artifact_group("gridgrouper", "1.2", "caGrid-gridgrouper", %w(common client))).
  merge!(cagrid_artifact_group(
      "service-security-provider", "1.2", "caGrid-ServiceSecurityProvider", %w(client))).
  merge!(
    :internet2_subject => cagrid_ivy("internet2", "subject", "0.2.1", "subject"),
    :internet2_grouper => cagrid_ivy("internet2", "grouper",   "1.1", "grouper", "grouper.jar")
    )
  )

###### TESTING

CGLIB = "net.sourceforge.cglib:com.springsource.net.sf.cglib:jar:2.1.3"

JUNIT = "org.junit:com.springsource.org.junit:jar:4.8.1"

EASYMOCK = struct(
  :main     => "org.easymock:easymock:jar:2.2",
  :classext => "org.easymock:easymockclassextension:jar:2.2.2",
  :cglib    => CGLIB
  )

HSQLDB = "org.hsqldb:com.springsource.org.hsqldb:jar:1.8.0.9"

DBUNIT = "org.dbunit:dbunit:jar:2.2"
