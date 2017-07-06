IRIDA Development Primer
========================

Important links
---------------
* IRIDA GitHub - https://github.com/phac-nml/irida
* IRIDA GitLab - http://gitlab-irida.corefacility.ca/ (only accessable from NML network)
* Documentation site - https://irida.corefacility.ca/documentation/
* Public website - https://irida.ca

Languages and Libraries
-----------------------

IRIDA is a Java application developed using Java 8.

#### Spring framework

Documentation: http://docs.spring.io/spring-framework/docs/4.2.3.RELEASE/spring-framework-reference/html/

IRIDA uses the Spring Framework as the main backbone of the application.  Spring is used to assist with many of the main functions of the application including configuration, dependency injection, MVC, REST API, Java persistance API management, and more.

For a better understanding of how Spring works, it's recommended that IRIDA developers attend a *Core Spring Training* course https://pivotal.io/training/courses/core-spring-training.

#### Apache Maven

Documentation: http://maven.apache.org/guides/

IRIDA uses Apache Maven for dependency management and build automation.  Maven allows developers to specify dependencies for a Java application and Maven will handle downloading all necessary required packages and ensuring they're available for developers on the Java classpath.  It also allows you to specify build lifecycles to automate packaging an application for distribution or execute code for development.

Maven settings and dependencies can generally be found in the `pom.xml` file in the IRIDA root directory.

#### Hibernate

Documentation: http://hibernate.org/orm/documentation/4.3/

Hibernate is used to map Java objects to database tables without the need for writing extra database code.  IRIDA uses hibernate through the Java Persistence API abstraction.

#### Other important libraries

* Thymeleaf - Web interface templating - http://www.thymeleaf.org/documentation.html
* JQuery - Javascript libraries - https://api.jquery.com/
* AngularJS - Javascript libraries and templating - https://docs.angularjs.org/api
* Jekyll - Documentation build - http://jekyllrb.com/docs/home/

Quick start development requirements
------------------------------------

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

Running and building IRIDA
--------------------------

#### Running a development server

An IRIDA development server can be run with the `run.sh` script available in the project root directory.  The script has one option `--create-db`.  Using this option will automatically drop and recreate the databse using test data.

Running the `run.sh` without arguments script is equivalent to running:

```bash
mvn clean jetty:run -Dspring.profiles.active=dev
```

Any arguments added after `run.sh` will be proxied to the `mvn ...` command.

#### Building IRIDA for release

Run the following:

```bash
mvn clean package -DskipTests
```

This will create the `.war` and `.zip` files for IRIDA release under the `target/` directory.

#### Building IRIDA documentation

Jekyll

Building new features
---------------------

### Informing users of changes

When adding new features we have a couple places we need to inform our users.  First is the `CHANGELOG.md` file found in the root of the project.  If you've added a feature, fixed a bug, or made any changes worthwhile of telling IRIDA users, other IRIDA developers, or administrators they should be mentioned here.  Next is the `UPGRADING.md` guide.  This file is used to to inform IRIDA system admins what steps need to be taken when upgrading from one version of IRIDA to another.  For example if you add anything to a configuration file, if there are changes which require an upgrade to the database, a workflow, or any dependencies, it should be mentioned here.

#### Database Updates

While in development we use Hibernate to manage our database changes, in production we use [Liquibase][]. 
Liquibase allows you to specify changesets to a database in incremental, database agnostic XML files.  In practice IRIDA requires MariaDB or MySQL, but it's still worthwhile to use a tool to properly manage the updates.  Liquibase ensures that all changes to the database are performed in the correct order, and manages this by keeping track of a hashcode of the last applied changeset.  When IRIDA is started, liquibase runs first to check if there are new changesets to be applied, and also that the current state of the database is in the format that IRIDA will be expecting.

When we're doing development and running IRIDA in the `dev` Spring profile you can directly make changes to the model classes and those changes will be reflected into the database.  Before creating a merge request you should add any changes that are made to the database to a new changeset XML file and test that the database is correctly built in the `prod` Spring profile.  It's also worthwhile to take a dump of a production IRIDA database and ensure that your Liquibase upgrade correctly migrates any data to your new format.

You can find the existing Liquibase changeset files in `/src/manin/resounces/ca/corefacility/bioinformatics/irida/database/changesets`.

Sometimes database changes are too complex to be able to use Liquibase XML files.  Conveniently Liquibase also allows you to apply change sets using Java code.  This mode is not recommended to use very often as you don't get some of the same change management features, but it's useful when you have a difficult migration.  If you need to use a change set written in Java, place it under the `ca.corefacility.bioinformatics.irida.database.changesets` package.

Version control
---------------

The IRIDA project uses Git, [GitLab][], and [GitHub][] for verison control purposes.  The main development server used is the NML's [GitLab][] site.  We use an internal repository so that we have greater control over how the code is managed, greater control over the testing servers, and allows us to have private conversations about issues.  Once a feature is pushed to the *development* or *master* branches of the project, it is automatically mirrored to our [GitHub][] site to give access ot public users.

External collaborators are welcomed to develop new features and should submit pull requests on IRIDA's [GitHub][] page.

### Branch structure

IRIDA's branch structure is loosely based on the [GitFlow](http://nvie.com/posts/a-successful-git-branching-model) branch model.  This model allows the team to develop multiple features in parallel without contaminating the main development branch, keeping merge requests sane, and allows for stable releases.

#### Branches:

* *development* - This is the main running development branch.  It represents the latest features that have been developed by the team.  Features here should be kept in a state that they can be released at any time.
* *master* - This is the release branch.  It should be kept at the latest stable release.
* feature branches - These should be created by the developers as they work on new additions to the application.  They should be branched off *development* and merged back once the feature is entirely complete and ready to release.
* hotfix branches - These branches will be created when there is an bug in the master branch which must be fixed immediately.  When these branches are complete they should be merged into both *development* and *master*.

#### Release tags & versioning scheme

Whenever code is merged into *master*, a release should be created.  To mart the release the person merging the code should create a git tag at the point of the merge.

```bash
git tag 0.version.subversion
```

Don't forget to push the tag when you're finished.

```bash
git push --tags
```

Once the tag has been pushed, the tag should have been automatically created on IRIDA's [GitHub][] site at https://github.com/phac-nml/irida/releases.  This release will be created as a tag, but will not be a full release until release notes and release files are uploaded to [GitHub][].  To do this, click *Edit* next to the new tag, enter the details from the `CHANGELOG.md` file for this release, and upload the `.war` and `.zip` files for this release.


Example workflow:

![Git workflow](images/git-flow.png)

#### Performing a release

http://gitlab-irida.corefacility.ca/snippets/30

IRIDA Codebase
--------------

IRIDA is organized as a fairly classic Java web application.  All files are found under the `ca.corefacility.bioinformatics.irida` package root.

* `config` - Configuration classes.  All Spring application config, web config, Maven config, and scheduled task configuration can be found here.
* `database.changesets` - Java Liquibase changesets.  See more about our liquibase usage in the [Database Updates section](#database_updates).
* `events` - Classes here handle the `ProjectEvent` structure in IRIDA.  These are the messages you can find on the IRIDA dashboard and project recent activity pages.
* `exceptions` - Java `Exception` classes written for IRIDA.
* `model` - IRIDA uses MVC.  These are the model classes.
* `pipeline.upload` - Classes used to communicate workflows, libraries, and histories to Galaxy.
* `processing` - IRIDA's file processing chain.  This contains classes used when processing files uploaded to IRIDA such as unzipping, FastQC, and quality control.
* `repositories` - Repositories used for communicating with IRIDA's database.  These classes generally use [Spring Data JPA][] for communicating with the database.
* `ria` - "Rich Internet Application", this is where the controller code and all Java code for the web interface is found.
* `security` - IRIDA's security layer.  You'll mostly find [Spring Security][]  classes within.  See more in the [security](#security) section.
* `service` - IRIDA's service layer.  Here you'll find the business logic for reading, saving, and manipulating data.
* `util` - General utility classes.  These are generally developer tools that don't fit anywhere else in the class structure.
* `validators` - Validation classes used to verify data being saved in the database is correct.
* `web` - IRIDA's REST API.



[GitHub]: (https://github.com/phac-nml/irida)
[GitLab]: (http://gitlab-irida.corefacility.ca/)
[Spring Data JPA]: (http://projects.spring.io/spring-data-jpa/)
[Spring Security]: (https://projects.spring.io/spring-security/)
[Liquibase]: (http://www.liquibase.org/documentation/)