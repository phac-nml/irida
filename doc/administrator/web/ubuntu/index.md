---
layout: default
search_title: "Ubuntu Installation Guide"
description: "Ubuntu-specific install guide for setting up IRIDA."
---

Ubuntu Installation Guide
=========================
Starting with a fresh, completely up-to-date system (Ubuntu Server 15.10).

Installing Software with `apt`
------------------------------
You can install the latest Oracle JRE on Ubuntu using a PPA. The `apt` repositories also contain an up-to-date version of Tomcat 7, so no complex configuration needs to take place for Ubuntu.

    #### Install Oracle Java 8 
    apt-get install --yes python-software-properties
    apt-add-repository --yes ppa:webupd8team/java
    apt-get update
    apt-get install --yes oracle-java8-installer

    #### Install tomcat7 and mariadb-server
    apt-get install --yes tomcat7 libtcnative-1 mariadb-server
    #### Tell tomcat7 where Java is installed
    sed -i 's/#JAVA_HOME=.*/JAVA_HOME=\/usr\/lib\/jvm\/java-8-oracle/' /etc/default/tomcat7

Starting Tomcat and MariaDB on startup
--------------------------------------

The last step is to make sure that Tomcat starts on startup:


    systemctl enable mysql # mysql is the service name for mariadb on Ubuntu
    systemctl start mysql
    systemctl enable tomcat7
    systemctl start tomcat7
