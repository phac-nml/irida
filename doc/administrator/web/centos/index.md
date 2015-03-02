---
layout: default
---

CentOS Installation Guide
=========================
This guide starts from a fresh, up-to-date install of CentOS 6.5.

Oracle Java 8
-------------
You must download and install Java 8 from Oracle: http://www.oracle.com/technetwork/java/javase/downloads/index.html

Download the JDK package, and download the `rpm` file with the correct architecture for your machine. Install the `rpm`.

Apache Maven
------------
The version of Maven that is distributed with CentOS 6.4 is very old. You must use at least Maven 2, though the latest version (3.X series) is recommended.

    MAVEN_HOME=/opt/maven
    
    mkdir -p $MAVEN_HOME

    pushd $MAVEN_HOME
    wget http://mirror.its.dal.ca/apache/maven/maven-3/3.2.1/binaries/apache-maven-3.2.1-bin.tar.gz
    tar xf apache-maven-3.2.1-bin.tar.gz
    ln -s apache-maven-3.2.1 current

Finally, add `/opt/maven/current/bin` to your `PATH` variable.

Apache Tomcat
-------------
The version of Tomcat that is distributed with CentOS 6.5 is still version 6. Parts of our application rely on Servlet 3.0; Tomcat 6 does not support Servlet 3.0, so Tomcat 7 must be manually installed. 

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
    wget http://mirror.its.dal.ca/apache/tomcat/tomcat-7/v7.0.54/bin/apache-tomcat-7.0.54.tar.gz
    # Assuming that you downloaded the .tar.gz version:
    tar xf apache-tomcat-7*.tar.gz
    pushd apache-tomcat-7*
    mv bin/{bootstrap.jar,catalina-tasks.xml,tomcat-juli.jar,setclasspath.sh} $TOMCAT_BIN
    ln -s $TOMCAT_BIN/setclasspath.sh /usr/bin/
    mv bin/catalina.sh /usr/sbin/dtomcat7
    mv conf/* $TOMCAT_CONFIG
    mv lib/* $TOMCAT_LIB
    popd

    git clone http://irida.corefacility.ca/gitlab/irida/irida-install-documentation.git
    pushd irida-install-documentation
    mv centos/tomcat7 /etc/init.d/
    mv centos/tomcat7.conf $TOMCAT_CONFIG
    chkconfig --add tomcat7

    echo "CATALINA_HOME=$TOMCAT_HOME" >> /etc/sysconfig/tomcat7

    chown -R tomcat:tomcat $TOMCAT_STAGING $TOMCAT_CONFIG $TOMCAT_LIB $TOMCAT_WEBAPPS $TOMCAT_HOME $TOMCAT_LOG $TOMCAT_TEMP $TOMCAT_WORK $TOMCAT_BIN

Configuring Apache HTTP Server
------------------------------
We configure Tomcat to only listen internally for connections and have the Apache HTTP server send commands internally to Tomcat. The purpose of this routing is to:

1. Allow the Apache HTTP server to host multiple sites (Tomcat doesn't have occupy port 80),
2. Reduce the attack surface on our server (Only one piece of software is listening externally).

These instructions are the same (save for configuration file locations) for all Linux distributions.

### Configuring Apache Tomcat
Open `/etc/tomcat7/server.xml` and find the `Connector` elements within the `Service` element. Comment out (or delete) all `Connector` elements that listen on port 8080 and 8443. The only `Connector` element that should be uncommented is the AJP 1.3 `Connector` on port 8009. The **only** active `Connector` element in your `Service` element should look like:

    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />

### Apache HTTP Server
You can use the apache configuration file that is part of this repository to configure your HTTP server:

    IRIDA_PARENT_STAGING=/tmp/irida-parent
    mkdir -p $IRIDA_PARENT_STAGING
    pushd $IRIDA_PARENT_STAGING

    git clone http://irida.corefacility.ca/gitlab/irida/irida-install-documentation.git
    pushd irida-install-documentation
    cp centos/ngs-archive.conf /etc/httpd/conf.d/ngs-archive.conf

The configuration file included as part of our example configuration is *not* suitable for production use -- you **must** configure `/etc/httpd/conf.d/ngs-archive.conf` to match your environment.

Starting HTTPD and Tomcat on Startup
------------------------------------
The last step is to make sure that `httpd` and `tomcat7` both start on startup:

    chkconfig httpd on
    service httpd start

    chkconfig tomcat7 on
    service tomcat7 start
