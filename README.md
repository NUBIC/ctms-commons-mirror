ctms-commons
============

This project is a collection of libraries for shared functionality
across the CTMS workspace, and particularly among the applications in
the Clinical Trials Suite.

Using
-----

ctms-commons is distributed on the [NCI CBIIT public ivy
repository][cbiit-ivy].  The organization is `gov.nih.nci.cabig.ctms`.

It has dependencies from the public caGrid 1.2, NCI-CBIIT, and central
Maven 2 repositories.  See `ivy/ivysettings.xml` for URLs and
patterns.

[cbiit-ivy]: https://wiki.nci.nih.gov/display/BuildandDeploymentAutomation/Ivy-+How+to+retrieve+files+from+Ivy

Development
-----------

### Changes

No commits that affect the public API may be made without an
associated entry in the changelog.

### Building

This library is built with buildr.  As of this writing, it works with
buildr 1.4.6 on jruby 1.6.2.  It uses ivy4r for ivy integration.

To set up a new development environment, install jruby and then use
jruby to run `install_gems.rb` in the root of the project.

#### CI

The CI build is at https://public-ci.nubic.northwestern.edu/job/ctms-commons/ .

Javadoc snapshots are at https://public-ci.nubic.northwestern.edu/job/ctms-commons%20javadoc/javadoc/ .

### Testing

#### Databases

The suite-authorization module has integrated tests that are either
PostgreSQL or Oracle-backed.  In order to run these tests,
successfully, you'll need to

1) Create a database for them to use
2) Create a properties file in `build/` pointing to the
   database. There's a sample in that directory.  Name the first one
   of these files `csm-test-connection.properties` (more on that in a
   moment).
3) Run `buildr ctms-commons:suite:authorization:test:wipe_db` to
   initialize the schema.

You can keep several database configurations (e.g. for testing on
PostgreSQL vs. Oracle).  You can switch between them using the CSM_DB
environment variable:

    $ buildr ctms-commons:suite:authorization:test:wipe_db CSM_DB=csm-oracle
    $ buildr test CSM_DB=csm-oracle

This would look for the configuration in
`build/csm-oracle.properties`.  The default for CSM_DB is
`csm-test-connection`.

##### PostgreSQL

If you get an error like this:

    org.apache.tools.ant.BuildException: org.postgresql.util.PSQLException: ERROR: language "plpgsql" does not exist
     Hint: Use CREATE LANGUAGE to load the language into the database.

Run this command as a database superuser in your PostgreSQL test
database:

    CREATE LANGUAGE plpgsql;

### Releasing

In order to release ctms-commons, you have to build a release package
and submit it to CBIIT. They will review it and include it in the
[Nexus][] release repository.

[Nexus]: https://wiki.nci.nih.gov/display/sysdeploy/Sonatype+Nexus+Maven+Repository+Manager+%28CBIIT%29

#### Versions

ctms-commons tries to follow the rational versioning policy.  In
summary that means that the library version numbers go
[major].[minor].[patch]:

  * [patch] is updated for bugfixes only.
  * [minor] is updated for new features which do not break backwards
    compatibility.
  * [major] is updated for breaking changes.  This should be very rare.

An exception is where the [major] value is 0.  New modules in the
library can be initially released with major=0 while the API is being
hashed out.  Libraries with major=0 are expected to maintain backwards
compatibility only within minor versions (e.g., 0.1.0 -> 0.1.1).  Once
the API is firm, these modules should be released under the same
version scheme as the remainder of the library.

Going forward, ctms-commons will have many minor releases as an
alternative to publishing snapshot builds.

#### Steps to release

 0) Ensure that everything you want to go into the release has been
    committed into trunk.  Ensure that the CHANGELOG is up to date.
 1) Set the version number in the buildfile to the appropriate release
    version.  E.g., 3.9.4.RELEASE.
 2) Commit.
 3) Run `buildr publish:prepare`.  This will do a clean build, and
    stage the artifacts for publication.
 4) Check that the new artifacts (under nexus-staging) are as you
    expect.
 5) Run `buildr publish:zip`.
 6) Upload the resulting ZIP file to any NCI server.
 7) E-mail the application support desk to with the location where you
    uploaded the new release artifacts and request they be added to
    Nexus.
 8) Run `buildr publish:tag`.
 9) Update the version number in the buildfile to the next development
    version.  E.g., 3.9.5.DEV.
10) Commit.

(Note that buildr provides a release process, but we aren't using it
because CBIIT doesn't allow us direct access to the release repo.)