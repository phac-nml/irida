---
layout: default
---

Ubuntu Installation Guide
=========================
Starting with a fresh, completely up-to-date system (Ubuntu Server 14.04).

Installing Software with `apt`
------------------------------
You can install the latest Oracle JRE on Ubuntu using a PPA. The `apt` repositories also contain an up-to-date version of Tomcat 7, so no complex configuration needs to take place for Ubuntu.

    #### Install Oracle Java 8 
    apt-get install --yes python-software-properties
    apt-add-repository --yes ppa:webupd8team/java
    apt-get update
    apt-get install --yes oracle-java8-installer

    #### Install tomcat7, apache http server
    apt-get install --yes tomcat7 libtcnative-1 apache2 libapache2-mod-proxy-html
    #### Enable apache mod_proxy
    a2enmod proxy_http proxy_ajp
    #### Tell Tomcat where Java 8 is installed:
    sed -i 's/#JAVA_HOME=.*/JAVA_HOME=\/usr\/lib\/jvm\/java-8-oracle/' /etc/default/tomcat7
    service tomcat7 start

Configuring Apache HTTP Server
------------------------------
We configure Tomcat to only listen internally for connections and have the Apache HTTP server send commands internally to Tomcat. The purpose of this routing is to:

1. Allow the Apache HTTP server to host multiple sites (Tomcat doesn't have occupy port 80),
2. Reduce the attack surface on our server (Only one piece of software is listening externally).

These instructions are the same (save for configuration file locations) for all Linux distributions.

### Configuring Apache Tomcat
Open `/etc/tomcat7/server.xml` and find the `Connector` elements within the `Service` element. Comment out (or delete) all `Connector` elements that listen on port 8080 and 8443. The only `Connector` element that should be uncommented is the AJP 1.3 `Connector` on port 8009. The **only** active `Connector` element in your `Service` element should look like:

    <Connector port="8009" protocol="AJP/1.3" redirectPort="8443" />
    
Open `/etc/default/tomcat7` and find the `JAVA_OPTS` key, which should look similar to below:

    JAVA_OPTS="-Djava.awt.headless=true -Xmx128m -XX:+UseConcMarkSweepGC"
    
The `-Xmx128m` option sets the maximum Java heap space to 128MB.  Increase this value a bit to give IRIDA a bit more memory, for example to `-Xmx2048m`.

Also, make sure the `JAVA_HOME` key points to the correct location where Java is installed.

### Configuring `apache2`
You can use the apache configuration file that is part of this repository to configure your HTTP server:

    IRIDA_PARENT_STAGING=/tmp/irida-parent
    mkdir -p $IRIDA_PARENT_STAGING
    pushd $IRIDA_PARENT_STAGING

    git clone http://irida.corefacility.ca/gitlab/irida/irida-install-documentation.git
    pushd irida-parent
    cp ubuntu/ngs-archive.conf /etc/apache2/sites-available/ngs-archive

You **MUST** configure `/etc/apache2/sites-available/ngs-archive` to match your environment. After you've made your configuration changes, enable the NGS Archive configuration file by linking into `sites-enabled` in the Apache configuration directory:

    pushd /etc/apache2/sites-enabled
    ln -s ../sites-available/ngs-archive
    service apache2 restart
