IRIDA API
=========
This project contains *only* the back-end Java API and database interaction projects. This project is the core of project IRIDA. You can use an existing front-end for IRIDA (such as `irida-web` or `irida-vaadin`) or implement your own front-end.

Installing Dependencies
=======================
The IRIDA API project depends on FastQC and a custom version of Galaxy Bootstrap, but these aren't distributed as Maven artifacts. We distribute a copy of FastQC and Galaxy Dependencies (binaries and source code) in the `lib/` directory. You can quickly install the files in the `lib/` directory by doing the following:

    git submodule update --init --recursive
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

        ########################################################
        # Uploader configuration for uploading files to Galaxy #
        ########################################################

        # The URL Galaxy is being run
        galaxy.uploader.url=http://localhost/

        # An email address of an administrator account on Galaxy
        galaxy.uploader.admin.email=admin@localhost

        # An API key for the above admin account
        galaxy.uploader.admin.apiKey=xxxx

        # How to store data on Galaxy.  One of 'remote' or 'local'.
        ## remote:  Uploads a copy of a file to Galaxy.
        ## local:  Uploads only a filesystem location (link) of a file to Galaxy.  Assumes
        ##  files are shared between the NGS Archive and Galaxy (e.g. over NFS).
        galaxy.uploader.dataStorage=local

        ###########################################
        # Execution Manager configuration Galaxy  #
        ###########################################
        
        # The URL for the Galaxy execution manager
        galaxy.execution.url=http://localhost/

        # The API key of an account to run workflows in Galaxy.
        # This does not have to be an administrator account.
        galaxy.execution.apiKey=xxxx

        # The email address of an account to run workflows in Galaxy
        galaxy.execution.email=user@localhost

        # The data storage method for uploading data into a Galaxy execution manager.
        galaxy.execution.dataStorage=local


3. A workflow must be installed within the database.  This can be accomplished by running the following command:

        mvn clean package -DskipTests # to clean and package code

        mvn exec:java -Dexec.cleanupDaemonThreads=false -Dexec.mainClass="ca.corefacility.bioinformatics.irida.config.workflow.InstallRemoteWorkflowPhylogenomics" -Dexec.args="--username [username] --password [password] --workflowId [id] --inputSequenceLabel [label] --inputReferenceLabel [label] --outputTreeName [tree name] --outputMatrixName [matrix name] --outputSnpTableName [snp table name]"

   Where the above options can be found within the Galaxy workflow.

4. The workflow must be configured as the current workflow to use within IRIDA by adding a configuration parameter.  This must be added to `/etc/irida/irida.conf`.

        # The id of a phylogenomics workflow within Galaxy.
        # The corresponding workflow must already exist within the IRIDA database to be valid.
        galaxy.execution.workflow.phylogenomics.id=xxxx

Please see the main IRIDA installation guide for more details.

Running the Tests
=================

The unit tests for the IRIDA API can be run with `mvn test`.  The full set of tests can be run with `mvn verify`.

Some of the tests require downloading and setting up a version of Galaxy.  By default, this will download the Galaxy version definined in the _test.galaxy.*_ properties in the *pom.xml* file.  This can be overridden by setting these properties when running maven.  For example, to test against the very latest Galaxy code you can run:

	mvn verify -Dtest.galaxy.repository.url=https://bitbucket.org/galaxy/galaxy-central -Dtest.galaxy.branch=default -Dtest.galaxy.revision=latest

Miscellaneous Doodads
=====================
Installing the API project *without* executing tests:

    mvn clean install -DskipTests

