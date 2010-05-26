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

def spring_osgi_apache_commons(name, version)
  "org.apache.commons:com.springsource.org.apache.commons.#{name}:jar:#{version}"
end

SLF4J = struct(
  :api    => "org.slf4j:slf4j-api:jar:#{SLF4J_VERSION}",
  :jcl    => "org.slf4j:jcl-over-slf4j:jar:#{SLF4J_VERSION}",
  :simple => "org.slf4j:slf4j-simple:jar:#{SLF4J_VERSION}"
  # :log4j  => "org.slf4j:log4j-over-slf4j:jar:#{SLF4J_VERSION}",
  # :jul    => "org.slf4j:jul-to-slf4j:jar:#{SLF4J_VERSION}"
  )

JAKARTA_COMMONS = struct(
  :collections => spring_osgi_apache_commons("collections", "3.2.0"),
  :lang        => spring_osgi_apache_commons("lang", "2.4.0"),
  :io          => spring_osgi_apache_commons("io", "1.4.0")
)

HIBERNATE = struct(
  :main               => "org.hibernate:com.springsource.org.hibernate:jar:3.3.1.GA",
  :annotations        => "org.hibernate:com.springsource.org.hibernate.annotations:jar:3.4.0.GA",
  :annotations_common => "org.hibernate:com.springsource.org.hibernate.annotations.common:jar:3.3.0.ga",
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

###### TESTING

CGLIB = "net.sourceforge.cglib:com.springsource.net.sf.cglib:jar:2.1.3"

JUNIT = "org.junit:com.springsource.org.junit:jar:4.8.1"

EASYMOCK = struct(
  :main     => "org.easymock:easymock:jar:2.2",
  :classext => "org.easymock:easymockclassextension:jar:2.2.2",
  :cglib    => CGLIB
  )

HSQLDB = "org.hsqldb:com.springsource.org.hsqldb:jar:1.8.0.9"
