---
layout: default
---

This guide describes how to set up a workstation for hacking on IRIDA. 

* This comment becomes the table of contents
{:toc}

General Requirements
====================

You're required to install a few different pieces of software on your machine before you can get started on hacking IRIDA:

1. DB: A MySQL or MariaDB server,
2. Languages: Java 8 (we have a hard requirement on Java 8), Python, Node
3. Build: Apache Maven, Bower
4. SCM: Git
5. IDE: Eclipse, Netbeans, IntelliJ, vim... (whatever you want, really).

Install instructions
====================

Install instructions for Ubuntu
-------------------------------
These instructions work for a fresh, up-to-date install of Ubuntu 14.04 LTS. No guarantees if you're using some other version of Ubuntu or some derivative of Ubuntu.

    # required for using the `apt-add-repository` command
    sudo apt-get install --yes --no-install-recommends python-software-properties
    # add the ppa for downloading the oracle java installer
    sudo apt-add-repository --yes ppa:webupd8team/java
    # update sources
    sudo apt-get update

    # You will be prompted to set a root password for mysql and to accept the license for the Java installer.
    sudo apt-get install --yes --no-install-recommends mysql-server maven git oracle-java8-installer


Install instructions for CentOS
-------------------------------
These instructions work for a fresh, up-to-date install of CentOS 7. No guarantees if you're using some other version of CentOS or some other RedHat-like distro.

CentOS (unfortunately) isn't as easy to set up as Ubuntu, you will have to manually install several bits of software.

Post-software install setup (Ubuntu and CentOS)
-----------------------------------------------
Once you've installed all of the prerequisites with your package manager (or manually), you can proceed with configuring MySQL (creating a user account and setting permissions), checking out IRIDA from our git repository, and running IRIDA on the command-line. You're on your own for IDE configuration.

### Cloning IRIDA

You've *probably* already figured out how to clone IRIDA if you're reading this documentation. Nevertheless, for completeness sake, you can clone IRIDA on the command-line like so:

    git clone https://github.com/phac-nml/irida.git

Importing IRIDA into an IDE is left to the developer.

### Installing dependencies

IRIDA uses some custom libraries that are either a) not currently available in a Maven repository, or b) a custom version of the software available in Maven central. We provide a convenient `bash` script for installing our additional Java dependencies in the `lib` directory:

    cd irida/lib/
    bash install-libs.sh

### Custom configuration

Many of IRIDA's properties can be configured by creating a file `/etc/irida/irida.conf`. The file is a basic key/value file (keys and values are separated by an `=` sign). A short example:

    # configure liquibase to manage the database schema
    liquibase.update.database.schema=false
    # disable hibernate's automatic schema creation
    hibernate.hbm2ddl.auto=
    # disallow hibernate from importing any data
    hibernate.hbm2ddl.import_files=

The sections below will include the keys that you can use to override the default behaviour.

### Configure MySQL

IRIDA is configured to use the following credentials by default:

* **Username** (`jdbc.username`): `test`
* **Password** (`jdbc.password`): `test`
* **Database** (`jdbc.url`): `jdbc:mysql://localhost:3306/irida_test`

You'll need to permit those user credentials to create the tables in the database. A quick one-liner for that is:

    echo "grant all privileges on irida_test.* to 'test'@'localhost' identified by 'test';" | mysql -u root -p

Also create the database if it doesn't exist:

    echo "create database irida_test;" | mysql -u root -p

In order to run local integration tests, the database `irida_integration_test` is used instead.  This database requires permissions from user `'test'@'localhost'`.  To grant such permissions a quick one-liner is:

    echo "grant all privileges on irida_integration_test.* to 'test'@'localhost';" | mysql -u root -p

### Configure Filesystem Locations

IRIDA stores much of its metadata in the relational database, but all sequencing and analysis files are stored on the filesystem. Directory configuration is:

* **Sequencing Data**: `sequence.file.base.directory`
* **Reference Files**: `reference.file.base.directory`
* **Analysis Output**: `output.file.base.directory`

If the directories that are configured do not exist (they don't likely exist if you don't configure them), IRIDA will default to automatically creating a temporary directory using Java's [`Files.createTempDirectory`](http://docs.oracle.com/javase/8/docs/api/java/nio/file/Files.html#createTempDirectory-java.lang.String-java.nio.file.attribute.FileAttribute...-).

### Testing IRIDA

You can verify that you've installed everything correctly in one of two ways:

1. Minimal verification: Check to see that Jetty starts, or
2. Maximal verification: Run the complete test suite.

Checking to see that Jetty starts will ensure that you're able to start hacking on the UI or the REST API. If you're going to be working on Galaxy-related features, you should *probably* run the complete test suite as it runs a Docker version of Galaxy and verifies that communication between IRIDA and Galaxy will work properly. Keep in mind that the complete test suite execution currently takes approximately 1 hour to complete.

#### Checking to see that Jetty starts

IRIDA uses Maven for build and dependency management. You can check to see that Jetty starts like so:

    mvn clean jetty:run
    
#### Database first-time setup

When first starting up Jetty, you'll need to have the database created and populated, which can be done by running the following script:

    ./run.sh --create-db
    
This will create the database schema and import some testing data. This can also be used to drop then recreate the database and reimport the starting dataset when a clean database is needed.

For all subsequent runs, simply run the script with no options:

    ./run.sh
    
This will update the database if the schema has been changed, but without dropping all of the tables beforehand, which will cause Jetty to start up much faster.

#### Integration Testing

To run the full integration test suite for IRIDA please run the following:

    ./run-tests.sh all

This will run all the integration test profiles using Maven, and print out reports for each profile.

Setting up Galaxy
-----------------

Please refer to the [Galaxy Install Guide][galaxy-install] for information on setting up Galaxy to use with IRIDA. The simplest method is to use Docker, but if new tools are being developed for Galaxy and integrated into IRIDA it is recommended to install a non-Docker version of Galaxy.

Front End Development Setup
---------------------------

IRIDA uses [Yarn](https://yarnpkg.com) for front-end dependency management.  [Install yarn](https://yarnpkg.com/en/docs/install) by following the directions for your machine.  For production, it is automatically installed during the build process.

From within `src/main/webapp/`:

You will need to run `yarn install` to install all dependencies required to run the following from the command line.

Tasks
=====

* `yarn sass` - compile all `.scss` files into `.css` files, with sourcemaps.
* `yarn build` - compile all es6 files to es2015.
* `yarn start` - use when developing front end code.  Webpack will monitor for changes to the entry files, compile them, and then updates the browser with the changes.

JavaScript and SCSS Code Formatting and Linting
===============================================

IRIDA JavaScript follows the [Prettier](https://github.com/prettier/prettier) format guideline and is enforced using `eslint-plugin-prettier` (see below for enabling eslint in IntelliJ and VS Code).

There is good [editor integration for Prettier formatting](https://github.com/prettier/prettier#editor-integration)

### IntelliJ IDEA Setup

To enable eslinting (JavaScript linting) in IntelliJ, open the preferences panel > Languages & Frameworks > JavaScript > Code Quality Tools > ESLint

* Check the "Enable" checkbox.
* Node interpreter should be you system node by default.
* ESLint package is the `eslint` within the projects `node_modules` directory.
* Configuration file is the `src/main/webapp/.eslintrc.json`. 

<video controls="controls" style="width: 960px">
    <source src="images/intellij-eslint.mp4" type="video/mp4" />
</video>   
 
### Visual Studio Code Setup

To enable eslinting (JavaScript linting) in VS Code:

 * Open the quick open panel `Ctrl+P`.
 * Copy and paste: `ext install vscode-eslint` and press enter.
 * Select `ESLint` and click "Install".
 * Click "Enable" and allow VS Code to restart.

<video controls="controls" style="width: 960px">
    <source src="images/vs-code-eslint.mp4" type="video/mp4" />
</video>   

[galaxy-install]: ../../administrator/galaxy
