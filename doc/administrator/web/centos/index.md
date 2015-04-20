---
layout: default
search_title: "CentOS Installation Guide"
description: "CentOS-specific install guide for setting up IRIDA."
---

CentOS Installation Guide
=========================
This guide starts from a fresh, up-to-date install of CentOS 6.5.

Oracle Java 8
-------------
You must download and install Java 8 from Oracle: <http://www.oracle.com/technetwork/java/javase/downloads/index.html>

Download the JDK package, and download the `rpm` file with the correct architecture for your machine. Install the `rpm`.

Apache Maven
------------
The version of Maven that is distributed with CentOS 6.5 is very old. You must use at least Maven 2, though the latest version (3.X series) is recommended.

```bash
MAVEN_HOME=/opt/maven

mkdir -p $MAVEN_HOME

pushd $MAVEN_HOME
wget http://mirror.its.dal.ca/apache/maven/maven-3/3.3.1/binaries/apache-maven-3.3.1-bin.tar.gz
tar xf apache-maven-3.3.1-bin.tar.gz
ln -s apache-maven-3.3.1 current
```

Finally, add `/opt/maven/current/bin` to your `PATH` variable.

Apache Tomcat
-------------
The version of Tomcat that is distributed with CentOS 6.5 is still version 6. Parts of our application rely on Servlet 3.0; Tomcat 6 does not support Servlet 3.0, so Tomcat 7 (or higher) must be manually installed. The instructions below

* install the latest Tomcat 7 release, using a similar directory structure to the way that Tomcat 7 is installed in later versions of CentOS when using `yum`.
* assume that you're going to create a system user called `tomcat` to run the instance of Tomcat.
* assume that you want to use the same `init` script that we've provided

These instructions are provided for convenience, and are not required to be followed. You can safely skip this section if you already have an instance of Tomcat installed, or if you want to manually install and configure Tomcat.

```bash
TOMCAT_STAGING=/opt/tomcat7
TOMCAT_CONFIG=/etc/tomcat7
TOMCAT_LIB=/usr/share/java/tomcat7
TOMCAT_WEBAPPS=/var/lib/tomcat7/webapps
TOMCAT_LOG=/var/log/tomcat7
TOMCAT_TEMP=/var/cache/tomcat7/temp
TOMCAT_HOME=/usr/share/tomcat7
TOMCAT_WORK=/var/cache/tomcat7/work
TOMCAT_BIN=$TOMCAT_HOME/bin

mkdir -p $TOMCAT_STAGING $TOMCAT_CONFIG $TOMCAT_LIB $TOMCAT_WEBAPPS $TOMCAT_HOME $TOMCAT_LOG $TOMCAT_TEMP $TOMCAT_WORK $TOMCAT_BIN
pushd $TOMCAT_HOME
ln -s $TOMCAT_CONFIG conf
ln -s $TOMCAT_LIB lib
ln -s $TOMCAT_WEBAPPS webapps
ln -s $TOMCAT_LOG logs
ln -s $TOMCAT_TEMP temp
ln -s $TOMCAT_WORK work
popd

useradd --home $TOMCAT_HOME --system tomcat

pushd $TOMCAT_STAGING
# Get the latest Tomcat 7 from: https://tomcat.apache.org/download-70.cgi (or uncomment the line below)
# wget http://mirror.its.dal.ca/apache/tomcat/tomcat-7/v7.0.59/bin/apache-tomcat-7.0.59.tar.gz
# Assuming that you downloaded the .tar.gz version:
tar xf apache-tomcat-7*.tar.gz
pushd apache-tomcat-7*
mv bin/{bootstrap.jar,catalina-tasks.xml,tomcat-juli.jar,setclasspath.sh} $TOMCAT_BIN
ln -s $TOMCAT_BIN/setclasspath.sh /usr/bin/
mv bin/catalina.sh /usr/sbin/dtomcat7
mv conf/* $TOMCAT_CONFIG
mv lib/* $TOMCAT_LIB
popd

wget -O /etc/init.d/tomcat7 {{ site.url }}/administrator/web/centos/etc/init.d/tomcat7
wget -O $TOMCAT_CONFIG/tomcat7.conf {{ site.url }}/administrator/web/centos/etc/tomcat7/tomcat7.conf
chkconfig --add tomcat7

echo "CATALINA_HOME=$TOMCAT_HOME" >> /etc/sysconfig/tomcat7

chown -R tomcat:tomcat $TOMCAT_STAGING $TOMCAT_CONFIG $TOMCAT_LIB $TOMCAT_WEBAPPS $TOMCAT_HOME $TOMCAT_LOG $TOMCAT_TEMP $TOMCAT_WORK $TOMCAT_BIN
```

Configuring Apache HTTP Server
------------------------------
We configure Tomcat to only listen internally for connections and have the Apache HTTP server send commands internally to Tomcat. The purpose of this routing is to:

1. Allow the Apache HTTP server to host multiple sites (Tomcat doesn't have to occupy port 80),
2. Reduce the attack surface on our server (Only one piece of software is listening externally).

These instructions are the same (save for configuration file locations) for all Linux distributions.

As with the [Apache Tomcat](#apache-tomcat) section, these instructions are provided for convenience. IRIDA does *not* depend on Apache Tomcat, or Apache HTTP server; you may install the IRIDA web interface on *any* Servlet container that supports Servlet >3.0, and proxy through *any* HTTP server that you want, if you want.

### Apache HTTP Server
You can use the apache configuration file that is part of this repository to configure your HTTP server:

```bash
wget -O /etc/httpd/conf.d/irida.conf {{ site.url }}/administrator/web/centos/etc/httpd/conf.d/irida.conf
```

**Note**: The configuration file included as part of our example configuration is *not* complete! You **must** configure `/etc/httpd/conf.d/irida.conf` to match your environment.

Starting HTTPD and Tomcat on Startup
------------------------------------
The last step is to make sure that `httpd` and `tomcat7` both start on startup:

```bash
chkconfig httpd on
service httpd start

chkconfig tomcat7 on
service tomcat7 start
```
