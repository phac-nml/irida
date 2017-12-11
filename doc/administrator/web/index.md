---
layout: default
search_title: "IRIDA Web Interface Install Guide"
description: "Install guide for setting up the IRIDA web interface."
---

This document describes how to install the IRIDA web interface. We assume that you have completed [installing and configuring Galaxy](../galaxy).

* This comment becomes the table of contents
{:toc}

The IRIDA platform currently consists of three, separate components:

1. The web interfaces: User interface and API,
2. Galaxy, and
3. Command-line clients.

The IRIDA Web interfaces are intended to be deployed in a Servlet container, supporting Servlet 3.0 or higher. You can download IRIDA as a pre-packaged `WAR` file at <https://irida.corefacility.ca/downloads/webapp/irida-latest.war>.

Prerequisites
=============

The following prerequisites are required for running the IRIDA web interfaces:

* [Java](http://www.oracle.com/technetwork/java/index.html) 8 or higher.
* A working servlet container supporting Servlet 3.0 ([Tomcat](https://tomcat.apache.org/), version 7 or higher, for example)
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
Deploying IRIDA mainly involves deploying the `WAR` file into your Servlet container, but does also require some configuration outside of your Servlet container.

Servlet Container Configuration
-------------------------------
Two environment variables needs to be set in your Servlet container for IRIDA to function correctly: `spring.profiles.active=prod`.

You can adjust these variables in Tomcat by editing (depending on your distribution) `/etc/tomcat/tomcat.conf` (CentOS) or `/etc/default/tomcat7` (Ubuntu), and finding the `JAVA_OPTS` variable and setting the variables as shown below:

    JAVA_OPTS="-Dspring.profiles.active=prod"

Core Configuration
------------------
All IRIDA configuration files are stored in `/etc/irida/`. The main IRIDA configuration file should be written to `/etc/irida/irida.conf`. `irida.conf` is a plain, Java `properties` file (so a key-value pair file). You can download the file from [config/irida.conf](config/irida.conf), or you can find an example below:

{% highlight ini %}
{% include_relative config/irida.conf %}
{% endhighlight %}

If this file does not exist, the platform will use internal configuration values. The internal configuration values point at a local instance of the database server. The likelihood of the internal configuration values correspond to your production environment is alarmingly low (or at least, should be).

The main configuration parameters you will need to change are:

1. **Directories to store files managed by IRIDA:**
  * `sequence.file.base.directory=/opt/irida/data/sequence` - Sequence files managed by IRIDA.
  * `reference.file.base.directory=/opt/irida/data/reference` - Reference files assigned to projects in IRIDA.
  * `output.file.base.directory=/opt/irida/data/output` - Results of analysis pipelines.
2. **Threads used for file processing (FastQC, GZip, etc):**
  * `file.processing.core.size=4` - The initial number of threads available for file processing.
  * `file.processing.max.size=8` - The maximum number of available threads for file processing.  This number should not exceed the configured maximum number of JDBC threads.
  * `file.processing.queue.capacity=512` - The maximum number of file processing jobs that can be queued.
2. **Database connection information:**
  * `jdbc.url=jdbc:mysql://localhost:3306/irida_test`
  * `jdbc.username=test`
  * `jdbc.password=test`
3. **Galaxy connection information for executing pipelines:**
  * `galaxy.execution.url=http://localhost/`
  * `galaxy.execution.apiKey=xxxx`
  * `galaxy.execution.email=user@localhost`
  * `irida.workflow.max-running=4` - The maximum number of running workflows.  For larger installations this number can be increased.
4. **NCBI SRA export configuration** - An SRA bulk upload user account must be created with NCBI to allow automated SRA uploads.  See [NCBI SRA Handbook](http://www.ncbi.nlm.nih.gov/books/NBK47529/#_SRA_Quick_Sub_BK_Establishing_a_Center_A_) for details.
  * `ncbi.upload.host` - FTP host to upload ncbi exports
  * `ncbi.upload.user` - FTP Username
  * `ncbi.upload.password` - FTP password
  * `ncbi.upload.baseDirectory` - base directory in which to create SRA submissions
  * `ncbi.upload.namespace` - Prefix for file upload identifiers to NCBI. The namespace is used to guarantee upload IDs are unique.  This configuration option is used as a placeholder and may still be set by the user.

Web Configuration
-----------------
The IRIDA platform also looks for a web application configuration file at `/etc/irida/web.conf`.  Similar to the irida.conf file, this file is a plain Java configuration file.  The properties in this file will be used to configure parameters of the web application component of the IRIDA platform.  You can download the file from [config/web.conf](config/web.conf), or you can find an example below:

{% highlight ini %}
{% include_relative config/web.conf %}
{% endhighlight %}

If this file does not exist the platform will use internal configuration values which will probably not correspond to your production environment.

The `mail.server.*` configuration parameters will need to correspond to a configured mail server, such as [Postfix][].  This will be used by IRIDA to send email notifications to users on the creation of an account or on password resets.

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

You can download the `WAR` file from: <https://irida.corefacility.ca/downloads/webapp/irida-latest.war>

Tomcat's deployment directory is typically some variation of `/var/lib/tomcat/webapps/`. Deploying the `WAR` file in Tomcat is as simple as moving the `WAR` file you downloaded into that directory.

On startup, IRIDA will:

1. Automatically prepare the database on your system (using [Liquibase](http://liquibase.org)) using the database connection details you specified in [Core Configuration](#core-configuration).
2. Install any internally configured workflows.
3. Configure the connection to Galaxy.

If IRIDA has successfully been deployed, you should be able to use your web browser to navigate to <http://localhost:8080/irida-latest/> (assuming you're deploying to the local machine, and also assuming that you've left the `WAR` file named `irida-latest.war`).

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

[Postfix]: http://www.postfix.org/
