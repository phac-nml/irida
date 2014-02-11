IRIDA API
=========
This project contains *only* the back-end Java API and database interaction projects. This project is the core of project IRIDA. You can use an existing front-end for IRIDA (such as `irida-web` or `irida-vaadin`) or implement your own front-end.

Installing Dependencies
=======================
The IRIDA API project depends on FastQC and a custom version of Galaxy Bootstrap, but these aren't distributed as Maven artifacts. We distribute a copy of FastQC and Galaxy Bootstrap (binaries and source code) in the `lib/` directory. You can quickly install the files in the `lib/` directory by doing the following:

    cd $IRIDA_API_ROOT/lib
    ./install-libs.sh

In addition, some of the integration tests requires setting up an instance of [Galaxy](https://wiki.galaxyproject.org/Admin/GetGalaxy).  This is handled automatically using Galaxy Bootstrap, but requires [Mecurial](http://mercurial.selenic.com/) and [Python](http://www.python.org/) to be installed.  On Ubuntu, these can be installed with `sudo apt-get install mercurial python`.

Setting Up MySQL for IRIDA
==========================
Your computer will probably have MySQL installed already.  If not, there are many tutorials online for how to install.

The IRIDA-api project uses Hibernate and hbm2ddl.auto to manage entities in the database.  hbm2ddl.auto will automatically create the tables that are required for entities to be stored as long as the expected schema and user is available.

Setting up the IRIDA schema
----------------------------
1. Open mysql as the root user 
   * mysql -u root -p
2. Create a schema named irida_test
   * CREATE DATABASE irida_test;

Creating the IRIDA test user
-----------------------------
1. Open mysql as the root user 
   * mysql -u root -p
2. Create a user named "test"
   * CREATE USER 'test'@'localhost' IDENTIFIED BY 'test';
3. Grant privileges to the test user
   * GRANT ALL PRIVILEGES ON irida_test.* to 'test'@'%';

The irida-api package should now be able to run.

Running the Tests
=================

The unit tests for the IRIDA API can be run with `mvn test`.  The full set of tests can be run with `mvn verify`.

Some of the tests require downloading and setting up a version of Galaxy.  By default, this will download the latest release of Galaxy in the stable branch from https://bitbucket.org/galaxy/galaxy-central.  If changes have been made in Galaxy which break the tests then the tests can be run with a previous revision number of Galaxy by setting the `test.galaxy.revision` property.  For example:

	mvn verify -Dtest.galaxy.revision=6c5913a4b701813e823638125fff8bf9fda7354b

Miscellaneous Doodads
=====================
Installing the API project *without* executing tests:

    mvn clean install -Dmaven.test.skip=true

