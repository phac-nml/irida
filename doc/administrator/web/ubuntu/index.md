---
layout: default
search_title: "Ubuntu Installation Guide"
description: "Ubuntu-specific install guide for setting up IRIDA."
---

Ubuntu Installation Guide
=========================
Starting with a fresh, completely up-to-date system (Ubuntu 24.04).

Installing Software with `apt`
------------------------------

    #### Install OpenJDK Java 17
    sudo apt-get install openjdk-17-jdk

    #### Install mariadb-server
    sudo apt-get install --yes mariadb-server

    #### Download and Install tomcat9
    sudo mkdir /opt/tomcat

    For security reasons, it's best not to run Tomcat as the root user so create a dedicated user and group
    sudo useradd -m -U -d /opt/tomcat -s /bin/false tomcat

    Download [Tomcat 9](https://tomcat.apache.org/download-90.cgi)
    sudo tar -xvf /PATH/TO/DOWNLOADED/apache-tomcat-9.0.98.tar.gz -C /opt/tomcat

    Change ownership of Tomcat Directory
    sudo groupadd tomcat
    sudo usermod -a -G tomcat tomcat
    sudo chown -R tomcat:tomcat /opt/tomcat

    Update the ownership for the data directories that you have set in `/etc/irida/irida.conf`

    Configure Tomcat as a service
    sudo touch /etc/systemd/system/tomcat.service
    nano /etc/systemd/system/tomcat.service

    Add the following contents to the tomcat.service file above and then save.

    ```
    [Unit]
    Description=Tomcat Server
    After=network.target

    [Service]
    Type=forking
    User=tomcat
    Group=tomcat
    Environment="JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64"
    WorkingDirectory=/opt/tomcat/apache-tomcat-9.0.98
    ExecStart=/opt/tomcat/apache-tomcat-9.0.98/bin/startup.sh

    [Install]
    WantedBy=multi-user.target
    ```

    Reload the systemd daemon
    sudo systemctl daemon-reload

Starting Tomcat and MariaDB on startup
--------------------------------------

The last step is to make sure that Tomcat starts on startup:

    sudo systemctl enable mysql # mysql is the service name for mariadb on Ubuntu
    sudo systemctl start mysql
    sudo systemctl enable tomcat.service
    sudo systemctl start tomcat.service
