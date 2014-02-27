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

Setup of Galaxy
===============

To link up the API with a running instance of Galaxy, the following steps need to be taken.

1. Install [Galaxy](https://wiki.galaxyproject.org/Admin/GetGalaxy) and setup an administrator user.
   * Make sure to set the property `allow_library_path_paste = True` within the `universe_wsgi.ini` configuration file to properly upload files.
2. Construct a configuration file with the Galaxy connection parameters.  This should be located within `/etc/irida/irida.conf` and contain the following information:

        # The URL where Galaxy is being run
        galaxy.url=http://localhost/

        # An email address of an administrator account on Galaxy
        galaxy.admin.email=admin@localhost

        # An API key for the above admin account
        galaxy.admin.apiKey=xxxx

        # How to store data on Galaxy.  One of 'remote' or 'local'.
        ## remote:  Uploads a copy of a file to Galaxy.
        ## local:  Uploads only a filesystem location (link) of a file to Galaxy.  Assumes
        ##  files are shared between the NGS Archive and Galaxy (e.g. over NFS).
        galaxy.dataStorage=local

Please see the main IRIDA installation guide for more details.

Running the Tests
=================

The unit tests for the IRIDA API can be run with `mvn test`.  The full set of tests can be run with `mvn verify`.

Some of the tests require downloading and setting up a version of Galaxy.  By default, this will download the revision definined in the *test.galaxy.revision* in the *pom.xml* file for the stable branch from https://bitbucket.org/galaxy/galaxy-dist.  This can be set to *latest* to test against the latest version of Galaxy.  This value can be overridden by running, for example:

	mvn verify -Dtest.galaxy.revision=latest

Miscellaneous Doodads
=====================
Installing the API project *without* executing tests:

    mvn clean install -Dmaven.test.skip=true

