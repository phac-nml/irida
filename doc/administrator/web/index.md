---
layout: default
search_title: "IRIDA Web Interface Install Guide"
description: "Install guide for setting up the IRIDA web interface."
---

This document describes how to install the IRIDA web interface. We assume that you have either downloaded IRIDA as a `WAR` file distributable, or have [built IRIDA from source](./building), and also assume that you have completed [installing and configuring Galaxy](../galaxy).

* This comment becomes the table of contents
{:toc}

The IRIDA platform currently consists of three, separate components:

1. The web interfaces: User interface and API,
2. Galaxy, and
3. Command-line clients.

The IRIDA Web interfaces are intended to be deployed in a Servlet container, supporting Servlet 3.0 or higher. You can either build IRIDA from source, or download a pre-packaged `WAR`.

Building IRIDA from source
==========================
If you would like to build IRIDA from source, please see the instructions for [building IRIDA](building/).

Prerequisites
=============

The following prerequisites are required for running the IRIDA web interfaces:

* [Java](http://www.oracle.com/technetwork/java/index.html) 8 or higher.
* A working servlet container supporting Servlet 3.0 ([Tomcat](https://tomcat.apache.org/), version 7 or higher, for example)
* A working HTTP server (not required, but recommended, depending on your set up)
* A working database server (the application is tested on [MySQL](https://www.mysql.com/) or [MariaDB](https://mariadb.org/)).
* A working install of Galaxy (we recommend that you run Galaxy and the IRIDA web interface on separate machines).

The install guide assumes that you are using [Bash](https://www.gnu.org/software/bash/manual/bashref.html)

Prerequisite install instructions
---------------------------------

We provide *some* instructions for installing and setting up your production environment. If you are comfortable installing and configuring a servlet container, or if your production environment has already been configured, you can safely skip this section.

* [CentOS](centos/)
* [Ubuntu](ubuntu/)
* **Windows**: Since the IRIDA Platform web interfaces are written in Java, they can, in theory, be deployed on a Windows host. We do not officially support or test deployment on Windows servers, so no instructions are provided.

Deploying IRIDA
===============
Whether you are [building IRIDA from source](./building) or installing a pre-built `WAR` file, you need to follow the instructions immediately below to configure your system.

Core Configuration
------------------
All IRIDA configuration files are stored in `/etc/irida/`. The main IRIDA configuration file should be written to `/etc/irida/irida.conf`. `irida.conf` is a plain, Java `properties` file (so a key-value pair file). You can download the file from [config/irida.conf](config/irida.conf), or you can find an example below:

{% highlight ini %}
{% include_relative config/irida.conf %}
{% endhighlight %}

If this file does not exist, the platform will use internal configuration values. The internal configuration values point at a local instance of the database server. The likelihood of the internal configuration values correspond to your production environment is alarmingly low (or at least, should be).

Web Configuration
-----------------
The IRIDA platform also looks for a web application configuration file at `/etc/irida/web.conf`.  Similar to the irida.conf file, this file is a plain Java configuration file.  The properties in this file will be used to configure parameters of the web application component of the IRIDA platform.  You can download the file from [config/web.conf](config/web.conf), or you can find an example below:

{% highlight ini %}
{% include_relative config/web.conf %}
{% endhighlight %}
    
If this file does not exist the platform will use internal configuration values which will probably not correspond to your production environment.

Analytics
---------
The IRIDA platform supports web analytics.  Include the analytic snippet inside a file in `/etc/irida/analytics/`.  The snippet will be injected into the page.

E.g. In `/etc/irida/analytics/google-analytics.html`.

{% highlight javascript %}
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-XXXXX-X']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
{% endhighlight %}

Deploy the `WAR` File
---------------------
Once you have adjusted the configuration files to your environment, you can deploy the `WAR` file to your servlet container.

On startup, IRIDA will:

1. Automatically prepare the database on your system (using [Liquibase](http://liquibase.org)) using the database connection details you specified in [Core Configuration](#core-configuration).
2. Install any internally configured workflows.
3. Configure the connection to Galaxy.

Logging in for the first time
-----------------------------

The first time IRIDA is run, it will add a default administrator user account to the database. Launch your web browser and navigate to the context where you've deployed IRIDA.

If everything has been configured correctly, you should see the IRIDA log-in page:

![IRIDA Log in page](irida-login.png)

The default administrator username and password are:

* **Username**: admin
* **Password**: password1

You will be required to change the password the first time you log-in with these credentials.

Once you've logged in for the first time, you will probably want to create some user accounts. User account creation is outlined in our [Administrative User Guide]({{ site.url }}/user/administrator).
