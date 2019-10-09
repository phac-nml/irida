---
layout: default
search_title: "Ubuntu Installation Guide"
description: "Ubuntu-specific install guide for setting up IRIDA."
---

Ubuntu Installation Guide
=========================
Starting with a fresh, completely up-to-date system (Ubuntu Server 18.04).

Installing Software with `apt`
------------------------------
You can install the latest OpenJDK JDK on Ubuntu. The `apt` repositories also contain an up-to-date version of Tomcat 8, so no complex configuration needs to take place for Ubuntu.

    #### Install OpenJDK Java 8 
    apt-get install openjdk-11-jdk

    #### Install tomcat8 and mariadb-server
    apt-get install --yes tomcat8 libtcnative-1 mariadb-server

Starting Tomcat and MariaDB on startup
--------------------------------------

The last step is to make sure that Tomcat starts on startup:


    systemctl enable mysql # mysql is the service name for mariadb on Ubuntu
    systemctl start mysql
    systemctl enable tomcat8
    systemctl start tomcat8
