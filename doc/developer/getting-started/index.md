---
layout: default
---

IRIDA Development Primer
========================
{:.no_toc}

This guide is for new developers to the IRIDA project to get a basic understanding of the layout of the project.

* This comment becomes the table of contents
{:toc}

Document History
----------------
{:.no_toc}

* Aug 18, 2017: Document creation.

Important links
---------------
* IRIDA GitHub - [https://github.com/phac-nml/irida](https://github.com/phac-nml/irida)
* IRIDA GitLab - [http://gitlab-irida.corefacility.ca/](http://gitlab-irida.corefacility.ca/) (only accessible from NML network)
* Documentation site - [https://irida.corefacility.ca/documentation/](https://irida.corefacility.ca/documentation/)
* Public information website - [https://irida.ca](https://irida.ca)

Languages and Libraries
-----------------------

IRIDA is a Java application developed using Java 8.

#### Spring framework
{:.no_toc}

Documentation: [http://docs.spring.io/spring-framework/docs/4.2.3.RELEASE/spring-framework-reference/html/](http://docs.spring.io/spring-framework/docs/4.2.3.RELEASE/spring-framework-reference/html/)

IRIDA uses the Spring Framework as the main backbone of the application.  Spring is used to assist with many of the main functions of the application including configuration, dependency injection, MVC, REST API, Java persistance API management, and more.

For a better understanding of how Spring works, it's recommended that IRIDA developers attend a *Core Spring Training* course https://pivotal.io/training/courses/core-spring-training.

#### Apache Maven
{:.no_toc}

Documentation: [http://maven.apache.org/guides/](http://maven.apache.org/guides/)

IRIDA uses Apache Maven for dependency management and build automation.  Maven allows developers to specify dependencies for a Java application and Maven will handle downloading all necessary required packages and ensuring they're available for developers on the Java classpath.  It also allows you to specify build lifecycles to automate packaging an application for distribution or execute code for development.

Maven settings and dependencies can generally be found in the `pom.xml` file in the IRIDA root directory.

#### Hibernate
{:.no_toc}

Documentation:[http://hibernate.org/orm/documentation/4.3/](http://hibernate.org/orm/documentation/4.3/)

Hibernate is used to map Java objects to database tables without the need for writing extra database code.  IRIDA uses hibernate through the Java Persistence API abstraction.

#### Liquibase
{:.no_toc}

Documentation: [http://www.liquibase.org/documentation/index.html](http://www.liquibase.org/documentation/index.html)

Liquibase is used to manage IRIDA's relational database change management.  Any time a change is made to IRIDA's production database schema, Liquibase is used to perform the change.  See the [Database Updates](#database-updates) section for more.

#### Galaxy
{:.no_toc}

Documentation: [https://docs.galaxyproject.org/en/master/index.html](https://docs.galaxyproject.org/en/master/index.html)

Galaxy is used as IRIDA's analysis workflow engine.  Analysis pipelines must be developed as Galaxy pipelines to integrate with IRIDA's workflow system.  See the [Galaxy Setup](/administrator/galaxy/) documentation for Galaxy installation and the [Tool Development](/developer/tools/) documentation for building tools for IRDIA.

#### Other important libraries
{:.no_toc}

* Thymeleaf - Web interface templating - http://www.thymeleaf.org/documentation.html
* JQuery - Javascript libraries - https://api.jquery.com/
* AngularJS - Javascript libraries and templating - https://docs.angularjs.org/api
* Jekyll - Documentation build - http://jekyllrb.com/docs/home/

Development platform
--------------------

The development platform used by most IRIDA developers is the [Eclipse](https://eclipse.org/ide/) IDE.  

The following plugins are recommended:

* Eclipse EGit - Git integration.  Helps by showing changes made to your codebase.
* Eclipse m2e - Maven integration.  Helps build your Maven project.

The following code formatting file should be imported into Eclipse for consistency between developers: [IRIDA eclipse code format](irida-code-format.xml)


Quick start development requirements
------------------------------------

An (incomplete) set of instructions for getting the IRIDA service layer and web front up and running for development on your Linux machine.  To include Galaxy and all the pipeline requirements, see the main IRIDA documentation.

* Clone IRIDA from the IRIDA [GitLab][].
* Install the following dependencies from your chosen package manager:
  * MariaDB
  * Java 8 JDK
  * Apache Maven
* Run the library installation script in the `lib/` directory:
```
cd irida/lib/
bash install-libs.sh
```
* Create a test database in MariaDB with the name `irida_test` and user `test` with password `test`.

From here you should be able to run the IRIDA service layer, REST API, and web UI using Jetty.

Running and building IRIDA
--------------------------

#### Running a development server

An IRIDA development server can be run with the `run.sh` script available in the project root directory.  The script has one option `--create-db`.  Using this option will automatically drop and recreate the databse using test data.

Running the `run.sh` without arguments script is equivalent to running:

```bash
mvn clean jetty:run -Dspring.profiles.active=dev
```

Any arguments added after `run.sh` will be proxied to the `mvn ...` command.

#### Spring profiles

Spring allows us to set profiles in the application that can be used to set up certain services for running in different environments.  IRIDA has the following profiles:

* `prod` - Production mode.  
  * Hibernate will not be allowed to make changes.  
  * Database will be managed by Liquibase.  
  * Attempt to connect to Galaxy to run workflows
  * Run all scheduled tasks such as NCBI uploads, data synchronization, etc.
* `dev` - Development mode.  
  * Hibernate to attempt to update the IRIDA database as you make code changes.
  * No galaxy connection.
  * No scheduled tasks.
* `it` - Integration test.
  * Liquibase used for database setup, but should only be used for integration testing.
* `test` - This profile is generally used when testing connecting to Galaxy.

When running IRIDA from the command line, a profile can be set by adding the following parameter:
```bash
-Dspring.profiles.active=YOURPROFILE
```

#### Running IRIDA tests locally

While GitLab CI runs all IRIDA's testing on every git push, it is often useful to run IRIDA's test suite locally for debugging or development.  IRIDA's test suite can be run with Maven using the `test` and `verify` goals.

See the [IRIDA tests](#irida-tests) section for more on how IRIDA's tests are developed.

##### Unit tests
{:.no_toc}

IRIDA's unit tests can be run with the following command:

```bash
mvn clean test
```

Maven will download all required dependencies and run the full suite of unit tests.  This will take a couple minutes and a report stating what tests passed and failed will be presented.

##### Integration tests
{:.no_toc}

IRIDA has 4 integration test profiles which splits the integration test suite into functional groups.  This allows GitLab CI to run the tests in parallel, and local test executions to only run the required portion of the test suite.  The 4 profiles are the following:

* `service_testing` - Runs the service layer and repository testing.
* `ui_testing` - Integration tests for IRIDA's web interface.
* `rest_testing` - Tests IRIDA's REST API.
* `galaxy_testing` - Runs all tests requiring a functional Galaxy instance.  This includes all analysis submission and pipeline tests.  This profile will automatically start a test galaxy instance to test with.

See the `<profiles>` section of the `pom.xml` file to see how the profiles are defined.

As the integration tests simulate a running IRIDA installation, in order to run any integration test the requirements needed to run a production IRIDA server must be installed on your development machine.  To run a test profile, run the following command:

```bash
mvn clean verify -P<TEST PROFILE>
```

The `ui_testing` profile requires additional parameters:

```bash
xvfb-run --auto-servernum --server-num=1 mvn clean verify -B -Pui_testing -Dwebdriver.chrome.driver=./src/main/webapp/node_modules/chromedriver/lib/chromedriver/chromedriver
```

Similar to the unit tests, Maven will download all dependencies, run the tests, and present a report.  Integration tests will take much longer than unit tests.

#### Building IRIDA for release

Run the following:

```bash
mvn clean package -DskipTests
```

This will create the `.war` and `.zip` files for IRIDA release under the `target/` directory.

#### Building IRIDA documentation

IRIDA documentation can be found in the `doc/` directory in the IRIDA root directory.  IRIDA's documentation is built using [Jekyll][].  Jekyll allows us to write documentation in Markdown format and it will convert the pages to HTML for releasing to the web.  The documentation at http://irida.corefacility.ca/documentation is all generated using this tool.

To test any documentation changes, you can `cd` into the `doc/` directory and run the following command:

```
jekyll serve
```

This command will read the `_config.yml` file in the directory for configuration settings, then serve the built documentation at http://localhost:4000.  As you make changes to documentation files it will automatically regenerate the documentation and reload its webserver.

To build the documentation for release, you can run the following:

```bash
mvn clean site
```

This will build the documentation HTML files into `doc/_site`.  That directory can be placed onto a web server for release.

IRIDA Codebase
--------------

IRIDA is organized as a fairly classic Java web application.  All main source can be found in the `src/main/` path.  Test code will be in `src/test/`

* `src/main/java` - IRIDA's Java source code.  All Java code for the main application is in this root.
* `src/main/webapp` - Web application code.  All web templates, Javascript, CSS, etc. is found here.
* `src/main/resources` - Configuration files, database update files, internationalization, and other scripts which are outside of the other `src/main` directories.
* `src/test/java` - Java test files.  IRIDA uses JUnit for testing.  See the [IRIDA tests](#irida-tests) section for more.
* `src/test/resources` - Additional files required for IRIDA testing.  These will generally be database files, test data, and test configuration.

### Java classpath

All files are found under the `ca.corefacility.bioinformatics.irida` package root.

* `config` - Configuration classes.  All Spring application config, web config, Maven config, and scheduled task configuration can be found here.
* `database.changesets` - Java Liquibase changesets.  See more about our liquibase usage in the [Database Updates section](#database-updates).
* `events` - Classes here handle the `ProjectEvent` structure in IRIDA.  These are the messages you can find on the IRIDA dashboard and project recent activity pages.
* `exceptions` - Java `Exception` classes written for IRIDA.
* `model` - IRIDA uses MVC.  These are the model classes which are persisted into the database using [Spring Data JPA][] and Hibernate.
* `pipeline.upload` - Classes used to communicate workflows, libraries, and histories to Galaxy.
* `processing` - IRIDA's file processing chain.  This contains classes used when processing files uploaded to IRIDA such as unzipping, FastQC, and quality control.
* `repositories` - Repositories used for communicating with IRIDA's database.  These classes generally use [Spring Data JPA][] for communicating with the database.
* `ria` - "Rich Internet Application", this is where the controller code and all Java code for the web interface is found.
* `security` - IRIDA's security layer.  You'll mostly find [Spring Security][]  classes within.  See more in the [security](#security) section.
* `service` - IRIDA's service layer.  Here you'll find the business logic for reading, saving, and manipulating data.
* `util` - General utility classes.  These are generally developer tools that don't fit anywhere else in the class structure.
* `validators` - Validation classes used to verify data being saved in the database is correct.
* `web` - IRIDA's REST API.

Building new features
---------------------

#### GitLab Issues

Any time a request comes in from a user for a new feature, or a bug is found, an *issue* should be created on [GitLab]().  The IRIDA project uses GitLab's issues list as it's main project tracking system.  The issue should be documented as fully as possible with the following:

* A general description of the problem/feature.
* What the expected functionality should be.
* Steps to reproduce the issue or how the feature should work.
* Who reported the bug or feature request.

Once an issue is completed it should be referenced in a [merge request](#merge-requests) in GitLab so the reviewer can know the full scope of the issue.

### Informing users of changes

When adding new features we have a couple places we need to inform our users.  First is the `CHANGELOG.md` file found in the root of the project.  If you've added a feature, fixed a bug, or made any changes worthwhile of telling IRIDA users, other IRIDA developers, or administrators they should be mentioned here.  Next is the `UPGRADING.md` guide.  This file is used to to inform IRIDA system admins what steps need to be taken when upgrading from one version of IRIDA to another.  For example if you add anything to a configuration file, if there are changes which require an upgrade to the database, a workflow, or any dependencies, it should be mentioned here.

### IRIDA tests

IRIDA uses JUnit for the majority of its testing.  To ensure the IRIDA codebase is performing as expected, when developing new features you should also write tests for the newly developed code.

IRIDA has 2 main types of tests:

#### Unit tests
{:.no_toc}

IRIDA unit tests are written entirely with JUnit and run with Maven Surefire.  Any classes or methods performing any sort of business logic should have unit tests written for them.  In general all test requirements should be mocked with Mockito, and tests should be written for expected behaviour, failure cases, and edge cases.  To mark a class as a unit test, the java file must be named with a `*Test.java` suffix.  For examples of existing IRIDA unit tests, see any classes under `src/test/java` class path `ca.corefacility.bioinformatics.irida.service.impl.unit`.

#### Integration tests
{:.no_toc}

IRIDA's integration tests are again developed using JUnit and run with Maven Failsafe.  In addition to the unit tests described above, IRIDA's integration tests verify that all components of the application work correctly together to produce the intended result.  Integration tests are generally written using the `SpringJUnit4ClassRunner` class which allows us to use a Spring application context and `@Autowired` to wire in test dependencies.  Mocking generally should not be used for dependencies in any integration tests.

As integration tests rely on the full application stack, database entries must be created at the beginning of each test.  To do this IRIDA uses the [DBUnit](http://dbunit.sourceforge.net/) library to load test data into the database prior to every test, and to clear the database after the test is completed.  Test database files are generally created for each test class, but some are reused between test classes.  DBUnit test files are written in an easy XML format.

Tests for different parts of the application may use additional libraries such as:

* [Selenium](http://www.seleniumhq.org/) for user interface testing.
* [REST-assured](http://rest-assured.io/) for REST API testing.

Refer to similar tests for examples of writing tests for the UI, REST API, etc.

To mark a class as a unit test, the java file must be named with a `*IT.java` suffix.  For examples of existing basic IRIDA integration tests, see classes under `src/test/java` class path `ca.corefacility.bioinformatics.irida.service.impl.integration`.

### Database Updates

While in development we use Hibernate to manage our database changes, in production we use [Liquibase][]. 

Liquibase allows you to create changesets for an application's database in incremental, database agnostic XML files.  In practice IRIDA requires MariaDB or MySQL, but it's still worthwhile to use a tool to properly manage the updates.  Liquibase ensures that all changes to the database are performed in the correct order, and manages this by keeping track of a hashcode of the last applied changeset.  When IRIDA is started, liquibase runs first to check if there are new changesets to be applied, and also that the current state of the database is in the format that IRIDA will be expecting.

When we're doing development, Liquibase is generally not used.  Instead we generally rely on Hibernate's HBM2DDL module which allows us to directly make changes to the model classes and those changes will be reflected into the database.This can be enabled by running IRIDA in the `dev` Spring profile.  Additionally when running in the `dev` profile example data from `src/main/resounrces/ca/corefacility/bioinformatics/irida/sql` will be loaded into the database for test purpose.  Since HBM2DDL is not to be used in production environments, before creating a merge request you should add any changes that are made to the database to a new changeset XML file and test that the database is correctly built in the `prod` Spring profile.  If you've modified any tables in the database it's also worth testing whether those changes can be properly migrated from an existing production database.  To do this you should take a dump of a production IRIDA database, load the dump up on your development machine, and run a server in `prod` profile to ensure the database upgrades correctly.

You can find the existing Liquibase changeset files in `/src/manin/resounces/ca/corefacility/bioinformatics/irida/database/changesets`.

Sometimes database changes are too complex to be able to use Liquibase XML files.  Conveniently Liquibase also allows you to apply change sets using Java code.  This mode is not recommended to use very often as you don't get some of the same change management features, but it's useful when you have a difficult migration.  If you need to use a change set written in Java, place it under the `ca.corefacility.bioinformatics.irida.database.changesets` package.

### Documentation

IRIDA has a number of sources of documentation.  For any user-facing changes, documentation should be added to the appropriate section of the user documentation under the `doc/` directory with instructions on how to use the new feature.

Developer documentation is also necessary for all Java classes, methods, code blocks, JavaScript, and any other code written for IRIDA.  In the Java portion of the IRIDA codebase, all methods and classes must have associated Javadoc.  To generate a Javadoc template for a method or class in Eclipse, type `/**` and press `<Enter>`.  A method/class description, all parameters, and return value should be documented.

Version control
---------------

The IRIDA project uses Git, [GitLab][], and [GitHub][] for version control purposes.  The main development server used is the NML's [GitLab][] site.  We use an internal repository so that we have greater control over how the code is managed, greater control over the testing servers, and allows us to have private conversations about issues.  Once a feature is pushed to the *development* or *master* branches of the project, it is automatically mirrored to our [GitHub][] site to give access ot public users.

External collaborators are welcomed to develop new features and should submit pull requests on IRIDA's [GitHub][] page.  Note that if we receive a GitHub pull request, NML IRIDA developers should pull the branch locally, then push it up to GitLab before merging to ensure the GitHub and GitLab repositories don't get out of sync.

### Branch structure
{:.no_toc}

IRIDA's branch structure is loosely based on the [GitFlow](http://nvie.com/posts/a-successful-git-branching-model) branch model.  This model allows the team to develop multiple features in parallel without contaminating the main development branch, keeping merge requests sane, and allows for stable and patchable releases.

#### Branches
{:.no_toc}

* *development* - This is the main running development branch.  It represents the latest features that have been developed by the team.  Features here should be kept in a state that they can be released at any time.
* *master* - This is the release branch.  It should be kept at the latest stable release.
* feature branches - These should be created by the developers as they work on new additions to the application.  They should be branched off *development* and merged back once the feature is entirely complete and ready to release.
* hotfix branches - These branches will be created when there is an bug in the master branch which must be fixed immediately.  When these branches are complete they should be merged into both *development* and *master*.

#### Release tags & versioning scheme
{:.no_toc}

Whenever code is merged into *master*, a release should be created.  To mark the release the person merging the code should create a git tag at the point of the merge.

```bash
git tag 0.version.subversion
```

Don't forget to push the tag when you're finished.

```bash
git push --tags
```

Once the tag has been pushed, the tag should have been automatically created on IRIDA's [GitHub][] site at https://github.com/phac-nml/irida/releases.  This release will be created as a tag, but will not be a full release until release notes and release files are uploaded to [GitHub][].  To do this, click *Edit* next to the new tag, enter the details from the `CHANGELOG.md` file for this release, and upload the `.war` and `.zip` files for this release.


Example workflow:

![Git workflow](git-flow.png)

#### Merge requests
{:.no_toc}

Code is not to be merged into the *development* or *master* branches by the developer who wrote the code.  Instead a merge request should be made on [GitLab][] and assigned to another developer on the project.  The reviewer should look over the code for issues, and anything that needs to be fixed should be mentioned in a comment in the merge request.  Once an issue has been fixed, the developer should push the changes to the merge request branch and mention the commit id in the comment so the reviewer can track the changes.

The reviewer of a merge request should ensure the following:

* The new or updated functionality works as expected.  This includes properly handling error cases.
* Any new functionality is well documented in Javadoc, inline code comments, and in the user guide.
* *CHANGELOG.md* and *UPGRADING.md* files are updated with necessary information.
* Any new features have appropriate unit and/or integration tests written and all tests are passing.
* New code is properly formatted using IRIDA's code formatter file.
* New code does not produce any Java errors or warnings.  Acceptable warnings may include *deprecated* warnings for methods or classes which should be refactored out.  See Eclipse's *Problems* panel for warnings produced.

If a merge request is a fix for an issue that is being tracked in [GitLab][], the developer should mention the issue number in the merge request with the format `Fixes #1234` so that the merge request will be linked to the issue and it will be automatically closed once the merge is complete.

When the reviewer is satisfied with the state of the branch to be merged, they should merge it into the *development* branch in [GitLab][] to close the request.

#### Performing a release
{:.no_toc}

After building a new `master` branch and releasing a tag, it is time to release a new version of IRIDA on its production servers.  To see more about the IRIDA release plan, see the following [GitLab][] snippet: http://gitlab-irida.corefacility.ca/snippets/30


[GitHub]: (https://github.com/phac-nml/irida)
[GitLab]: (http://gitlab-irida.corefacility.ca/)
[Spring Data JPA]: (http://projects.spring.io/spring-data-jpa/)
[Spring Security]: (https://projects.spring.io/spring-security/)
[Liquibase]: (http://www.liquibase.org/documentation/)
[Jekyll]: (http://jekyllrb.com/docs/home/)