---
layout: default
search_title: "CentOS Installation Guide"
description: "CentOS-specific install guide for setting up IRIDA."
---

CentOS Installation Guide
=========================
This guide starts from a fresh, up-to-date install of CentOS 7.

Installing prerequisites
------------------------

All required packages for the web interface are in the CentOS repositories. You can install them with the following commands:

    sudo yum -y install epel-release # for the apache native runtime
    sudo yum -y install apr tomcat java-11-openjdk-headless mariadb-server mariadb-client tomcat-native

Starting Tomcat and MariaDB on Startup
--------------------------------------
The last step is to make sure that Tomcat starts on startup:

```bash
sudo systemctl enable mariadb
sudo systemctl start mariadb
sudo systemctl enable tomcat
sudo systemctl start tomcat
```
