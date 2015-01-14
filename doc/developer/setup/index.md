---
layout: default
---

Developer Setup Guide
=====================

This guide describes how to set up a workstation for hacking on IRIDA. 

General Requirements
--------------------

You're required to install a few different pieces of software on your machine before you can get started on hacking IRIDA:

1. DB: A MySQL or MariaDB server,
2. Languages: Java 8 (we have a hard requirement on Java 8), Python, Node
3. Build: Apache Maven, Bower
4. SCM: Git and Mercurial (Git for IRIDA, Mercurial for Galaxy)
5. IDE: Eclipse, Netbeans, IntelliJ, vim... (whatever you want, really).

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

### Install software with `yum`

    sudo yum install

### Java 8
Start by downloading

Post-software install setup (Ubuntu and CentOS)
-----------------------------------------------
Once you've installed all of the prerequisites with your package manager (or manually), you can proceed with configuring MySQL (creating a user account and setting permissions), checking out IRIDA from our git repository, and running IRIDA on the command-line. You're on your own for IDE configuration.

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


