---
layout: default
search_title: "Galaxy Setup"
description: "Galaxy install guide"
---

Setup of a new Galaxy instance
==============================
{:.no_toc}

This document describes the necessary steps for installing and integrating [Galaxy][] with IRIDA as well as using Galaxy and [Galaxy Toolsheds][] to install workflows.

The following must be set up before proceeding with the installation.

1. A machine that has been set up to install Galaxy.  This could be the same machine as the IRIDA web interface, or (recommended) a separate machine.
2. A shared filesystem has been set up between IRIDA and Galaxy.  If Galaxy will be submitting to a compute cluster this filesystem must also be shared with the cluster.

* this comment becomes the table of contents.
{:toc}

Dependency Installation
-----------------------

The installation and setup of Galaxy requires a number of dependency software to be installed.  To install this software on CentOS (>= 6.6) please run:

	yum install mercurial pwgen python zlib-devel ncurses-devel tcsh git

The following dependencies are required for running or building some of the tools.

	yum groupinstall "Development tools"
	yum install db4-devel expat-devel java

### Conda Installation

Galaxy makes use of [conda][] for dependency installation of tools.  Conda can also be used to manage Galaxy software dependencies.  The easiest way to install conda is by downloading and installing [miniconda][].  E.g.,

```bash
wget https://repo.continuum.io/miniconda/Miniconda3-latest-Linux-x86_64.sh
bash Miniconda3-latest-Linux-x86_64.sh
```

This should default to installing conda under `~/miniconda3`.  For the remainder of these instructions we will assume conda is installed in this location, and that conda is available on your `PATH`.

*Note: conda requires the `bash` shell to fuction properly. To see which shell you are using you can run `echo $SHELL`. Also note that on some systems `/bin/sh` is simply a link to `/bin/bash`.*

### Conda Galaxy Environment

Galaxy requies a number of dependencies to be installed before it is run.  The easiest way to install these dependencies is through a conda environment.  Please create the initial environment and activate like so:

```bash
# Add necessary channels for software
conda config --add channels conda-forge
conda config --add channels defaults
conda config --add channels r
conda config --add channels bioconda

# Create conda environment and activate this environment
conda create --name galaxy python=2.7 samtools
source activate galaxy

# This installs some additional dependencies required by some of the IRIDA tools.
conda install perl-xml-simple perl-time-piece perl-bioperl openjdk gnuplot libjpeg-turbo
```

Galaxy Software Installation
----------------------------

This describes installing the main Galaxy software.  These instructions assume you are installing Galaxy version **v17.01**.  Older versions will also work, but any version < **v16.01** will require special modifications for some tools (see our [FAQ][faq-conda]). Newer versions should also work, but have not been thoroughly tested with IRIDA yet. Most of the installation documentation for Galaxy can be found at [GetGalaxy][].  In brief, these steps involve the following.

### Step 1: Download Galaxy

Please run the following commands to download Galaxy.

```bash
git clone https://github.com/galaxyproject/galaxy.git && cd galaxy
git checkout release_17.01
```

Once Galaxy is downloaded some additional modifications will be needed to configure Galaxy.  Please copy the configuration files from the sample configuration files like below before modifying:

```bash
# We assume you are in the galaxy/ directory.
cp config/galaxy.ini.sample config/galaxy.ini
cp config/tool_sheds_conf.xml.sample config/tool_sheds_conf.xml
```

### Step 2: Galaxy Database Setup

By default, Galaxy uses [SQLite][] for a database, but this is not sufficient for the larger workflows used by IRIDA.  We would recommend using [PostgreSQL][] or [MySQL][].  You will have to modify the property `database_connection` in the file `config/galaxy.ini` to point to your database. Please refer to the [Galaxy Database Setup][] guide for more details.  As an example, see below:

```
database_connection = postgresql://galaxy_user:password@localhost/galaxy_irida
```

### Step 3: Create Galaxy Environment Files

#### Galaxy web server environment

In order to make sure Galaxy uses the dependencies set up with conda, we need to make sure this environment is activated before Galaxy is run.  This can be accomplished by adding the following code to a file called `config/local_env.sh` (this file may not exist yet).

```bash
export PATH=~/miniconda3/bin:$PATH
source activate galaxy
```

Additionally, please change the shell used by Galaxy from `sh` to `bash` if necessary (that is, if `/bin/sh` is different from `/bin/bash`).  This can be done by changing `#!/bin/sh` to `#!/bin/bash` in the file `run.sh`.

#### Tool environments

Additionally, some Python dependencies and additional dependencies may be required by Galaxy on execution of tools.  This can be accomplished by creating another file `env.sh` and activating the conda **galaxy** environment here.  E.g.:

```bash
export PATH=~/miniconda3/bin:$PATH
source activate galaxy
```
Other steps will specify when you need to add additional instructions to this file.

### Step 4: Modify configuration file

The main Galaxy configuration file is located in `config/galaxy.ini`.  Please make the following changes to this file.  More information on this configuration file can be found at [Running Galaxy in a production environment][].

1. Modify the address that Galaxy should listen on for incoming connections to allow for connections external to the Galaxy server.
   * Change `#host = 127.0.0.1` to `host = 0.0.0.0`. (`0.0.0.0` listens on all interfaces and addresses)
2. Modify the port that Galaxy listens on so there are no conflicts with Tomcat (or other software).
    * E.g., change `#port = 8080` to `port = [some other port]`.
3. The below is necessary to allow direct linking of files in Galaxy to the IRIDA file locations.
   * Change `#allow_library_path_paste = False` to `allow_library_path_paste = True`.
4. Give the Galaxy **admin** and **workflow** users admin privileges (necessary for running workflows on linked files within Galaxy, see [create galaxy accounts](#step-1-create-galaxy-accounts)).
   * Change `#admin_users = None` to `admin_users = admin@localhost.localdomain,workflow@localhost.localdomain` (or whatever other users you wish to use).
5. Disable developer settings if enabled (from [Galaxy Disable Developer Settings][]).
   * Change `debug = True` to `debug = False`.
   * Change `use_interactive = True` to `use_interactive = False`.
   * Make sure `filter-with = gzip` is disabled.
6. Set the Galaxy id_secret for encoding database ids.
   * Change `#id_secret = USING THE DEFAULT IS NOT SECURE!` to `id_secret = some secure password`
      * The command `pwgen --secure -N 1 56` may be useful for picking a hard-to-guess key.
      * ***Note: Once this key is set, please do not change it.  This key is used to translate database ids in Galaxy to API ids used by IRIDA to access datasets, histories, and workflows.  IRIDA does store some of these API ids internally for debugging and tracking purposes and changing this value will render any of the API ids stored in IRIDA useless.***
7. Setup the Galaxy environment file `env.sh`.  This file is read by Galaxy to setup the environment for each tool.
   * Change `#environment_setup_file = None` to `environment_setup_file = env.sh`
8. Setup Conda for installing tool dependencies.
   * Set `conda_prefix = /home/galaxy-irida/miniconda3`, or wherever conda is installed for Galaxy.
   * Set `conda_ensure_channels = iuc,bioconda,r,defaults,conda-forge`.
9. Set the directory to install tool dependencies.
   * Set `#tool_dependency_dir = database/dependencies` to `tool_dependency_dir = database/dependencies` (uncomment).
   * You may also need to create the directory `database/dependencies` too.  E.g.,

      ```
      mkdir database/dependencies
      ```

### Step 5: Start up Galaxy

Verify that Galaxy can start by running:

	# Starts Galaxy and builds new database
	stdbuf -o 0 sh run.sh 2>&1 | tee run.sh.log

This will attempt to build the Galaxy database and start up Galaxy on <http://127.0.0.1:9090>.

*Note: `run.sh` builds and starts Galaxy, `tee` keeps a copy of the output, and `stdbuf` changes to no buffering to deal with pauses in output when running `tee`.  If `stdbuf` is not installed on your system you can just run `sh run.sh 2>&1 > run.sh.log` and `tail -f run.sh.log`.*

When complete you should see something similar to:

	Starting server in PID 8967.
	serving on 0.0.0.0:9090 view at http://127.0.0.1:9090

Once complete, Galaxy can be killed by pressing `CTRL+C`.

*Note: You may need to give port `9090` access through the firewall.  For CentOS this can be done by adding the line `-A INPUT -m state --state NEW -m tcp -p tcp --dport 9090 -j ACCEPT` to the file __/etc/sysconfig/iptables__ and then running `service iptables restart`.*

**Do not proceed if Galaxy does not start.**

### Step 6: Configure Galaxy as a service

Example scripts to configure Galaxy as a service can be found in the `contrib/` directory of Galaxy. Additional details can be found in the [Galaxy documentation][galaxy-production].  This guide assumes a Redhat distribution so we will be working with `contrib/galaxy.fedora-init`, but scripts for other systems are available.

1. If not already configured, create a non-root user for Galaxy.

   ```bash
   useradd --no-create-home --system galaxy-irida
   chown -R galaxy-irida galaxy/
   ```

2. Copy the startup script to the appropriate location.

   ```bash
   cp galax/contrib/galaxy.fedora-init /etc/init.d/galaxy
   ```

3. Make necessary modifications to variables in `/etc/init.d/galaxy` (user to run Galaxy, etc). For example:

   ```
   SERVICE_NAME="galaxy"
   RUN_AS="galaxy-irida"
   RUN_IN="/home/galaxy-irida/galaxy"
   ```

4. Enable Galaxy as a service.

   ```bash
   chkconfig galaxy on
   service galaxy start
   service galaxy status
   ```

### Step 7: Configure Galaxy Jobs Scheduler

The default job configuration is fine for running Galaxy on a single server or for evaluation purposes.  This will default to running all jobs on the local machine and limit to 4 jobs at any given time.

For more complicated job scheduling, please refer to the [Galaxy Job Config][] documentation.

### Step 8: Test out Galaxy

Once these steps are done, you should be able to connect to Galaxy by going to <http://galaxy-server-name:8080>.  If this works, please move on to the next step.  If this does not work, then please check the log file `galaxy/paster.log` for more details.

Configure Galaxy
----------------

Once Galaxy is up and running, there are a few steps needed in order to configure Galaxy with IRIDA.

### Step 1: Create Galaxy Accounts

To create the accounts in Galaxy for administration and workflow execution please log into Galaxy and go to **User > Register**.  Please use the same e-mail addresses as configured previously for the **admin** and **workflow**. You can configure to use only one account, **admin**, if you choose, or you can keep admin tasks and the IRIDA workflow executions separated using **admin** and **workflow** users.

### Step 2: Generate Workflow API Key

Please log in as the **workflow** user and go to **User > Preferences > Manage API Key** and click on **Create a new key**.  This will generate an API key for the user which is used by IRIDA to interact with Galaxy.  Please make note of this key for later when configuring IRIDA.

Galaxy Tools Installation
-------------------------

### Step 1: Configure External Toolsheds and Dependency Resolvers

The workflows used by IRIDA make use of external tools that can be installed using a [Galaxy Toolshed][].  The two toolsheds used by IRIDA are the [Main Galaxy Toolshed][] and the [IRIDA Toolshed][].  These are configured in the file `config/tool_sheds_conf.xml`.  Please open up this file and replace with the following:

```xml
<?xml version="1.0"?>
<tool_sheds>
	<tool_shed name="Galaxy main tool shed" url="http://toolshed.g2.bx.psu.edu/"/>
	<tool_shed name="IRIDA Galaxy Toolshed" url="https://irida.corefacility.ca/galaxy-shed"/>
</tool_sheds>
```

Now, re-start Galaxy with `service galaxy restart`.  If you log into Galaxy as the admin user and click on **Admin** in the top menu, then **Search Tool Shed**. In the menu at the left you should see the two configured toolsheds listed.

### Step 2: Install Pipeline Tools

#### Automated installation of tools

{%include administrator/galaxy/setup/automated-tool-install.md %}

#### Manual installation of tools

Alternatively, the necessary tools can be installed manually through the following instructions specific to each pipeline in IRIDA:

* [SNVPhyl Whole Genome Phylogeny][]
* [Assembly and Annotation][]
* [Assembly and Annotation Collection][]
* [SISTR Salmonella Typing][]
* [refseq_masher]
* [MentaLiST MLST][]
* [Bio_Hansel][]

Each of these will step through installing the necessary tools in IRIDA.  These steps will involve going to Galaxy, navigating to **Admin > Search tool sheds**, finding the appropriate tool and installing.  On completion, you should be able to go to **Admin > Manage installed tools** to check the status of each tool.  For a successfull install, you should see a status of `Installed`.  If there is an error, you can click on each tool for more details.

![galaxy-installed-repositories.jpg][]

All tools are, by default, installed in the directory `galaxy/../shed_tools` with binary dependencies installed in `galaxy/database/dependencies`.  Monitoring the install process of each tool can be done by monitoring the main Galaxy log file `paster.log`.

Link up Galaxy with IRIDA
-------------------------

In order to link up Galaxy with IRIDA please proceed through the following steps.

### Step 1: Install and configure the IRIDA web interface

Follow the instructions to [install and configure the IRIDA web interface][web].  In particular, you will need to modify the parameters **galaxy.execution.url**, **galaxy.execution.email**, and **galaxy.execution.dataStorage** in the file `/etc/irida/irida.conf`.

### Step 2: Test and monitor workflows

Once you have configured IRIDA to connect to Galaxy you can attempt to execute a workflow by adding some data to your cart, selecting  **Pipelines** from the main menu, then selecting a particular pipeline.  You will have to have some data uploaded into IRIDA before testing.  Currently all workflows assume you are using paired-end sequence reads.

Each workflow in IRIDA is run using Galaxy, and it's possible to monitor the status of a workflow or debug a workflow through Galaxy.  To do this, please log into Galaxy as the **workflow-user** and click on the **History Options** icon ![history-options-icon][] in the top-right of the **History** panel to view a list of saved histories.  You should see these histories being populated as you execute new workflows in IRIDA.

![saved-histories.jpg][]

[Galaxy]: https://wiki.galaxyproject.org/FrontPage
[GetGalaxy]: https://wiki.galaxyproject.org/Admin/GetGalaxy
[Running Galaxy in a production environment]: https://wiki.galaxyproject.org/Admin/Config/Performance/ProductionServer
[Galaxy Disable Developer Settings]: https://wiki.galaxyproject.org/Admin/Config/Performance/ProductionServer#disable-the-developer-settings
[Galaxy Database Setup]: https://wiki.galaxyproject.org/Admin/Config/Performance/ProductionServer#switching-to-a-database-server
[MySQL]: http://www.mysql.com/
[PostgreSQL]: http://www.postgresql.org/
[IRIDA Toolshed]: https://irida.corefacility.ca/galaxy-shed
[Main Galaxy Toolshed]: https://toolshed.g2.bx.psu.edu/
[Galaxy Toolshed]: https://wiki.galaxyproject.org/ToolShed
[Galaxy Toolsheds]: https://wiki.galaxyproject.org/ToolShed
[SNVPhyl Whole Genome Phylogeny]: ../pipelines/phylogenomics/
[SISTR Salmonella Typing]: ../pipelines/sistr/
[Assembly and Annotation]: ../pipelines/assembly-annotation/
[Assembly and Annotation Collection]: ../pipelines/assembly-annotation-collection/
[refseq_masher]: ../pipelines/refseq_masher/
[MentaLiST MLST]: ../pipelines/mentalist/
[Bio_Hansel]: ../pipelines/bio_hansel/
[Galaxy Job Config]: https://wiki.galaxyproject.org/Admin/Config/Jobs
[saved-histories.jpg]: ../images/saved-histories.jpg
[galaxy-installed-repositories.jpg]: ../images/galaxy-installed-repositories.jpg
[history-options-icon]: ../images/history-options-icon.jpg
[conda]: https://conda.io/docs/
[miniconda]: https://conda.io/miniconda.html
[galaxy-production]: https://galaxyproject.org/admin/config/performance/production-server/#groundwork-for-scalability
[SQLite]: https://www.sqlite.org/
[updating tbl2asn]: ../pipelines/assembly-annotation/#updating-tbl2asn
[faq-conda]: ../../faq/#installing-conda-dependencies-in-galaxy-versions--v1601
[web]: ../../web
