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
4. SCM: Git and Mercurial (Git for IRIDA, Mercurial for Galaxy)
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
    sudo apt-get install --yes --no-install-recommends mysql-server maven git mercurial oracle-java8-installer


Install instructions for CentOS
-------------------------------
These instructions work for a fresh, up-to-date install of CentOS 7. No guarantees if you're using some other version of CentOS or some other RedHat-like distro.

CentOS (unfortunately) isn't as easy to set up as Ubuntu, you will have to manually install several bits of software.

Post-software install setup (Ubuntu and CentOS)
-----------------------------------------------
Once you've installed all of the prerequisites with your package manager (or manually), you can proceed with configuring MySQL (creating a user account and setting permissions), checking out IRIDA from our git repository, and running IRIDA on the command-line. You're on your own for IDE configuration.

### Cloning IRIDA

You've *probably* already figured out how to clone IRIDA if you're reading this documentation. Nevertheless, for completeness sake, you can clone IRIDA on the command-line like so:

    git clone http://irida.corefacility.ca/gitlab/irida/irida.git

Importing IRIDA into an IDE is left to the developer.

### Installing dependencies

IRIDA uses some custom libraries that are either a) not currently available in a Maven repository, or b) a custom version of the software available in Maven central. We provide a convenient `bash` script for installing our additional Java dependencies in the `lib` directory:

    cd irida/lib/
    bash install-libs.sh

We're also using [Bower](http://bower.io/) and [Node](http://nodejs.org/) to manage our JavaScript dependencies, and [PhantomJS](http://phantomjs.org/) and [ChromeDriver](https://sites.google.com/a/chromium.org/chromedriver/) for UI testing. We **do not** recommend that you install the versions of Node and Bower that are available for download from the Ubuntu repositories -- they are ancient. You can manually install the current versions available from the project web sites, or you can run our `bash` script that will (at the very least) download more recent versions than what are available from the repositories:

    cd irida/
    bash ubuntu_install.sh

*Note*: The *only* Ubuntu-specific part of that script is where it installs the packages `ruby-sass` and `ruby-compass`. You can run this script on CentOS by commenting out that line.

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

Checking to see that Jetty starts will ensure that you're able to start hacking on the UI or the REST API. If you're going to be working on Galaxy-related features, you should *probably* run the complete test suite as it checks out a fresh version of Galaxy. Keep in mind that the complete test suite execution currently takes approximately 45 minutes to complete.

#### Checking to see that Jetty starts

IRIDA uses Maven for build and dependency management. You can check to see that Jetty starts like so:

    mvn clean jetty:run

#### Running the complete test suite

You can run the complete test suite like so:

    mvn clean verify

Setting up Galaxy
-----------------

Please see the article on [setting up Galaxy](galaxy).
