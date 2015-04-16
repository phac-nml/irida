---
layout: default
---

Galaxy Setup
============

This document describes the necessary steps for installing and integrating [Galaxy][] with IRIDA as well as using Galaxy and [Galaxy ToolSheds][] to install workflows.

* this comment becomes the table of contents.
{:toc}

IRIDA Galaxy Architecture
-------------------------

The overall architecture of IRIDA and Galaxy is as follows:

![irida-galaxy.jpg][]

1. IRIDA manages all input files for a workflow.  This includes sequencing reads, reference files, and the Galaxy workflow definition file.  On execution of a workflow, references to these files are sent to a Galaxy instance using the [Galaxy API][].  It is assumed that these files exist on a file system shared between IRIDA and Galaxy.
2. All tools used by a workflow are assumed to have been installed in Galaxy during the setup of IRIDA.  The Galaxy workflow is uploaded to Galaxy and the necessary tools are executed by Galaxy.  The default setup is to use the same machine for both the Galaxy Web/API and for execution of jobs.  For more advanced setup please refer to the [Galaxy Cluster Setup][] document.
3. Once the workflow execution is complete, a copy of the results are downloaded into IRIDA and stored in the shared filesystem.

The following must be set up before proceeding with the installation.

1. A machine that has been set up to install Galaxy.  This could be the same machine as the IRIDA web interface, or (recommended) a separate machine.
2. A shared filesystem has been set up between IRIDA and Galaxy.  If Galaxy will be submitting to a cluster this filesystem must also be shared with the cluster.  The [Galaxy Cluster Setup][] document describes this in more detail.

Installation Overview
---------------------

The main steps needed to install and integrate Galaxy with IRIDA are as follows.

* Dependency Installation
* Galaxy Database Setup
* Galaxy Software Installation
* Configure Galaxy
* Galaxy Tools Installation
* Link up Galaxy with IRIDA
* Configure Galaxy Data Cleanup

Environment Variables
---------------------

For the purpose of installation the following one-time environment variables will be used:

```bash 
# The system user the Galaxy software will run as.  For a clustered environment this account must be the same on all nodes of the cluster.
GALAXY_USER=galaxy-irida

# The base directory to setup Galaxy.  For a clustered environment this must be shared across all nodes of the cluster.
GALAXY_BASE_DIR=/home/galaxy-irida

# The root directory for the Galaxy software
GALAXY_ROOT_DIR=$GALAXY_BASE_DIR/galaxy-dist

# A special environment file used by Galaxy and the tools
GALAXY_ENV=$GALAXY_BASE_DIR/env.sh

# An email address for the admin user used to managed Galaxy (the address does not need to be a real email address).
GALAXY_ADMIN_USER=admin@localhost.localdomain

# An email address for the user used to run workflows in Galaxy (the address does not need to be a real email address).
GALAXY_WORKFLOW_USER=workflow@localhost.localdomain

# The mercurial commit number for the IRIDA Galaxy instance.  This should remain fixed as we have only tested against this commit number.
GALAXY_VERSION=b065a7a422d72c5436ba62bfc6d831a9df82a79f
```

Please customize these environment variables to your system and proceed through the rest of the instructions.  Please replace any instance of a variable (such as `$GALAXY_USER`) with the value for your system (such as `galaxy-irida`).  However, **do not** change the `$GALAXY_VERSION` variable; this revision is the version of Galaxy we have tested against.  To see more information about the particular version or other information on Galaxy releases please see [Galaxy News Page][] and [Bitbucket][].

For more information about installing Galaxy, please see [Running Galaxy in a production environment][].


Dependency Installation
-----------------------

The installation and setup of Galaxy requires a number of dependency software to be installed.  To install this software on CentOS (>= 6.6) please run:

	yum install mercurial nginx pwgen python

The following dependencies are required for running or building some of the tools.

	yum install db4-devel expat-devel java

Galaxy Database Setup
---------------------

For using Galaxy with IRIDA, we need to setup a specific database for Galaxy to record information.  Both [PostgreSQL][] and [MySQL][] are supported.  Please refer to the [Galaxy Database Setup][] guide for more details.

Galaxy Software Installation
----------------------------

This describes installing the main Galaxy software.  Most of the installation documentation for Galaxy can be found at [GetGalaxy][].  In brief, these steps involve the following.

### Step 1: Download and build Galaxy

Please run the following commands to download and build Galaxy.

```bash
cd $GALAXY_BASE_DIR
hg clone https://bitbucket.org/apetkau/galaxy-dist $GALAXY_ROOT_DIR
cd $GALAXY_ROOT_DIR
hg update $GALAXY_VERSION # switch to version of Galaxy used by IRIDA
hg id # verify Galaxy version
sh scripts/common_startup.sh 2>&1 | tee common_startup.log # Common startup tasks
cp $GALAXY_ROOT_DIR/config/galaxy.ini.sample $GALAXY_ROOT_DIR/config/galaxy.ini

mkdir $GALAXY_BASE_DIR/shed_tools # directory for installing tool shed tools
mkdir $GALAXY_BASE_DIR/tool_dependencies # directory for installing dependency binaries for tools
cp $GALAXY_ROOT_DIR/config/tool_sheds_conf.xml.sample $GALAXY_ROOT_DIR/config/tool_sheds_conf.xml
```

*Note: We are using the specific Galaxy version located at <https://bitbucket.org/apetkau/galaxy-dist/commits/b065a7a422d72c5436ba62bfc6d831a9df82a79f> which is based off of Galaxy version 15.03.1.  This was in order to fix an issue that was not fixed in the stable branch of Galaxy as of writing this document.  The specific issue was reported at <https://trello.com/c/I0n23JEP/2484-database-deadlock-for-large-workflows> with the fix in the main Galaxy code at <https://github.com/galaxyproject/galaxy/pull/52>.*

*Note: Dependening on the version of CentOS you use, you may encounter errors when attempting to clone the Galaxy bitbucket.org repository with Mercurial `hg clone`.  For Mercurial 1.4, this looks like the following:*

```
hg clone https://bitbucket.org/apetkau/galaxy-dist
destination directory: galaxy-dist
requesting all changes
abort: HTTP Error 400: Bad Request
```

*If this occurs, you will have to manually install a newer version of [Mercurial][], or manually download the specific revision of Galaxy used.* 

### Step 2: Create Galaxy Environment File

Some specific environment variables often need to be set for Galaxy and any of the tools running under Galaxy.  These can be configured in a special file `env.sh` which can be passed to Galaxy on startup and will be loaded by Galaxy and the tools.  Please create this file now by running.

	touch $GALAXY_ENV

Other steps will specify when you need to add setup instructions to this file.

### Step 3: Modify configuration file

The main Galaxy configuration file is located in `$GALAXY_ROOT_DIR/config/galaxy.ini`.  Please make the following changes to this file.  More information on this configuration file can be found at [Running Galaxy in a production environment][].

1. Modify the address that Galaxy should listen on for incoming connections to allow for connections external to the Galaxy server.
   * Change `#host = 127.0.0.1` to `host = 0.0.0.0`. (`0.0.0.0` listens on all interfaces and addresses)
2. Modify the port that Galaxy listens on so there are no conflicts with Tomcat.
    * Change `#port = 8080` to `port = 9090`.
3. Modify the Galaxy database connection string to connect to your specific database.
   * `database_connection = postgresql://galaxy:password@localhost/galaxy` (use a MySQL connection string if using MySQL or MariaDB)
4. The below is necessary to allow direct linking of files in Galaxy to the IRIDA file locations.
   * Change `#allow_library_path_paste = False` to `allow_library_path_paste = True`.
5. Give the `$GALAXY_ADMIN_USER` and `$GALAXY_WORKFLOW_USER` users in Galaxy admin privileges (necessary for running workflows on linked files within Galaxy).
   * Change `#admin_users = None` to `admin_users = $GALAXY_ADMIN_USER,$GALAXY_WORKFLOW_USER`.
6. Disable developer settings (from [Galaxy Disable Developer Settings][]).
   * Change `debug = True` to `debug = False`.
   * Change `use_interactive = True` to `use_interactive = False`.
   * Make sure `filter-with = gzip` is disabled.
7. Set directory for installing tools in Galaxy.
   * Change `#tool_dependency_dir = None` to `tool_dependency_dir = $GALAXY_BASE_DIR/tool_dependencies`
   * Uncomment `#tool_sheds_config_file = config/tool_sheds_conf.xml`.
8. Set the Galaxy id_secret for encoding database ids.
   * Change `#id_secret = USING THE DEFAULT IS NOT SECURE!` to `id_secret = some secure password`
      * The command `pwgen --secure -N 1 56` may be useful for picking a hard-to-guess key.
      * ***Note: Once this key is set, please do not change it.  This key is used to translate database ids in Galaxy to API ids used by IRIDA to access datasets, histories, and workflows.  IRIDA does store some of these API ids internally for debugging and tracking purposes and changing this value will render any of the API ids stored in IRIDA useless.***
9. Setup the Galaxy environment file `$GALAXY_ENV`.  This file is read by Galaxy to setup the environment for each tool.
   * Change `#environment_setup_file = None` to `environment_setup_file = $GALAXY_ENV`

### Step 4: Start up Galaxy

Verify that Galaxy can start by running:

	stdbuf -o 0 sh run.sh 2>&1 | tee run.sh.log # Builds Galaxy

This will attempt to build the Galaxy database and start up Galaxy on <http://127.0.0.1:9090>.

*Note: `run.sh` builds and starts Galaxy, `tee` keeps a copy of the output, and `stdbuf` changes to no buffering to deal with pauses in output when running `tee`.  If `stdbuf` is not installed on your system you can just run `sh run.sh 2>&1 > run.sh.log` and `tail -f run.sh.log`.*

When complete you should see something similar to:

	Starting server in PID 8967.
	serving on 0.0.0.0:9090 view at http://127.0.0.1:9090

Once complete, Galaxy can be killed by pressing `CTRL+C`.

*Note: You may need to give port `9090` access through the firewall.  For CentOS this can be done by adding the line `-A INPUT -m state --state NEW -m tcp -p tcp --dport 9090 -j ACCEPT` to the file __/etc/sysconfig/iptables__ and then running `service iptables restart`.*

**Do not proceed if Galaxy does not start.**

### Step 5: Configure Galaxy as a service

An example script [scripts/galaxy][] has been provided with this documentation in order to configure Galaxy as a service which starts at boot time.

The provided startup script uses a default system user named `galaxy-irida` to run Galaxy. You should either modify the default user, or create a `galaxy-irida` system user:

```bash
useradd --no-create-home --system galaxy-irida
```

Please make any necessary changes to this script and do the following:

```bash
sudo cp galaxy /etc/init.d/galaxy
sudo chkconfig galaxy on
sudo service galaxy start
sudo service galaxy status
```

The main changes you will need to make to this file are modifying some of the environment variables specific to your Galaxy installation.  These include:

```bash
GALAXY_USER=galaxy-irida
GALAXY_BASE_DIR=$GALAXY_BASE_DIR
GALAXY_ENV=$GALAXY_BASE_DIR/env.sh
GALAXY_ROOT_DIR=$GALAXY_BASE_DIR/galaxy-dist
GALAXY_RUN=$GALAXY_ROOT_DIR/run.sh
```

For more details, please refer to the [Running Galaxy in a production environment][] documentation.

### Step 6: Configure Galaxy Jobs Scheduler

There are many different ways to configure how Galaxy schedules jobs for execution.  The default method is to run all jobs on the local machine and limit to 4 jobs at any given time.  This can be modified by doing the following:

```bash
cp $GALAXY_ROOT_DIR/config/job_conf.xml.sample_basic $GALAXY_ROOT_DIR/config/job_conf.xml
```

Now please modify the file `$GALAXY_ROOT_DIR/config/job_conf.xml` and make any changes specific to your environment.  Please refer to the documentation [Galaxy Job Config][] for more information.

### Step 7: Test out Galaxy

Once these steps are done, you should be able to connect to Galaxy by going to <http://galaxy-server-name:9090>.  If this works, please move on to the next step.  If this does not work, then please check the log file `$GALAXY_ROOT_DIR/main.log` for more details.

Configure Galaxy
----------------

Once Galaxy is up and running, there are a few steps needed in order to configure Galaxy with IRIDA.

### Step 1: Create Galaxy Accounts

To create the accounts in Galaxy for administration and workflow execution please log into Galaxy and go to **User > Register**.  Please use the same e-mail addresses as configured previously, `$GALAXY_ADMIN_USER` and `$GALAXY_WORKFLOW_USER`.  `$GALAXY_ADMIN_USER` can be used to perform the administrative tasks whereas `$GALAXY_WORKFLOW_USER` can be used to run workflows within Galaxy.

### Step 2: Generate Workflow API Key

Please log in as the `$GALAXY_WORKFLOW_USER` and go to **User > API Keys** and click on **Generate a new key now**.  This will generate an API key for the user which is used by IRIDA to interact with Galaxy.  Please make note of this key for later when configuring IRIDA.

Galaxy Environment Setup
------------------------

Many of the tools in Galaxy are written in languages like Perl or Python and require specific modules to be installed which may be different from the system modules.  Please refer to the document [Galaxy Environment Setup][] for more information on how to create a customized environment specific to Galaxy.

Galaxy Tools Installation
-------------------------

### Step 1: Configure External Toolsheds

The workflows used by IRIDA make use of external tools that can be installed using a [Galaxy Toolshed][].  The two toolsheds used by IRIDA are the [Main Galaxy Toolshed][] and the [IRIDA Toolshed][].  These are configured in the file `$GALAXY_ROOT_DIR/config/tool_sheds_conf.xml`.  Please open up this file and replace with the following:

```xml
<?xml version="1.0"?>
<tool_sheds>
	<tool_shed name="Galaxy main tool shed" url="http://toolshed.g2.bx.psu.edu/"/>
	<tool_shed name="IRIDA Galaxy Toolshed" url="https://irida.corefacility.ca/galaxy-shed"/>
</tool_sheds>
```

Now, re-start Galaxy with `sudo service galaxy restart`.  If you log into Galaxy as the admin user and click on **Admin** in the top menu, then **Search and browse tool sheds**. In the menu at the left you should see the two configured toolsheds listed.

### Step 2: Install Pipeline Tools

The main pipelines included with IRIDA each require a specific set of tools to be installed in Galaxy.  Please refer to the documentation below for specific instructions on these workflows.

* [SNVPhyl Whole Genome Phylogeny][]
* [Assembly and Annotation][]

Each of these will step through installing the necessary tools in IRIDA.  These steps will involve going to Galaxy, navigating to **Admin > Search and browse tool sheds**, finding the appropriate tool and installing.  On completion, you should be able to go to **Admin > Manage installed tool shed repositories** to check the status of each tool.  For a successfull install, you should see a status of `Installed`.  If there is an error, you can click on each tool for more details.

![galaxy-installed-repositories.jpg][]

All tools are installed in the directory `$GALAXY_BASE_DIR/shed_tools` with binary dependencies installed in `$GALAXY_BASE_DIR/tool_dependencies`.  Monitoring the install process of each tool can be done by monitoring the main Galaxy log file `$GALAXY_BASE_DIR/main.log`.

Link up Galaxy with IRIDA
-------------------------

In order to link up Galaxy with IRIDA please proceed through the following steps.

### Step 1: Install and configure the IRIDA web interface

Follow the instructions to [install and configure the IRIDA web interface](../web).  In particular, you will need to modify the parameters **galaxy.execution.url**, **galaxy.execution.email**, and **galaxy.execution.dataStorage** in the file `/etc/irida/irida.conf`.

### Step 2: Test and monitor workflows

Once you have configured IRIDA to connect to Galaxy you can attempt to execute a workflow by adding some data to your cart, selecting  **Pipelines** from the main menu, then selecting a particular pipeline.  You will have to have some data uploaded into IRIDA before testing.  Currently all workflows assume you are using paired-end sequence reads.

Each workflow in IRIDA is run using Galaxy, and it's possible to monitor the status of a workflow or debug a workflow through Galaxy.  To do this, please log into Galaxy as the `$GALAXY_WORKFLOW_USER` and click on the **History Options** icon ![history-options-icon][] in the top-right of the **History** panel to view a list of saved histories.  You should see these histories being populated as you execute new workflows in IRIDA.

![saved-histories.jpg][]

Configure Galaxy Data Cleanup
-----------------------------

IRIDA stores and manages both the input files to an analysis workflow as well as the output files and provenance information from a workflow run through Galaxy.  In the process of running an analysis, many intermediate files are produced by Galaxy (SAM/BAM files, log files, etc), as well as intermediate data structures (Galaxy Data Libraries for storing input files to Galaxy, and the workflow uploaded to Galaxy).  These additional files and data structures are not stored or used by IRIDA following the completion of an analysis.

By default IRIDA will **not** remove any of the data generated and stored in Galaxy.  This provides additional resources beyond the output files and provenance information stored by IRIDA for each analysis.

However, some of the files produced by Galaxy can be quite large and may quickly fill up the storage capacity of the Galaxy server.  IRIDA can be instructed to clean up this data after a period of time by adjusting the parameter `irida.analysis.cleanup.days` in the main IRIDA configuration file `/etc/irida/irida.conf`.  This controls the number of days before IRIDA will remove analysis files from Galaxy.  This can be used to reduce the storage requirements for each analysis at the expense of not having any intermediate analysis files available.

Once the parameter `irida.analysis.cleanup.days` is set, IRIDA will periodically (once every hour) check for any analyses that have expired and clean up the necessary files in Galaxy.  However, these files will only be marked as **deleted** in Galaxy, not permanently removed.  To permanently remove these files, please do the following:

### Step 1: Create a Galaxy Cleanup script

The following is an example script that can be used to clean up **deleted** files in Galaxy.  Please save this script to `$GALAXY_ROOT_DIR/galaxy_cleanup.sh`, make executable with `chmod u+x $GALAXY_ROOT_DIR/galaxy_cleanup.sh`, and then make any necessary modifications to the variables.  In particular, please set `$GALAXY_ROOT_DIR` and modify `$DAYS_TO_KEEP` which defines the number of days since last access a deleted file in Galaxy will continue to exist before being removed from the file system.

```bash
#!/bin/sh

GALAXY_ROOT_DIR=/path/to/galaxy-dist
GALAXY_CONFIG=$GALAXY_ROOT_DIR/config/galaxy.ini
CLEANUP_LOG=$GALAXY_ROOT_DIR/galaxy_cleanup.log
DAYS_TO_KEEP=0

cd $GALAXY_ROOT_DIR

echo -e "\nBegin cleanup at `date`" >> $CLEANUP_LOG
echo -e "Begin delete useless histories" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -1 -r >> $CLEANUP_LOG
echo -e "\nBegin purge deleted histories" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -2 -r >> $CLEANUP_LOG
echo -e "\nBegin purge deleted datasets" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -3 -r >> $CLEANUP_LOG
echo -e "\nBegin purge deleted libraries" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -4 -r >> $CLEANUP_LOG
echo -e "\nBegin purge deleted library folders" >> $CLEANUP_LOG
python scripts/cleanup_datasets/cleanup_datasets.py $GALAXY_CONFIG -d $DAYS_TO_KEEP -5 -r >> $CLEANUP_LOG
echo -e "\nEnd cleanup at `date`" >> $CLEANUP_LOG
```

### Step 2: Schedule script to run using cron

Once this script is installed, it can be scheduled to run periodically by adding a cron job for the Galaxy user.  To do this, please run `crontab -e` and past the following line (replacing `$GALAXY_ROOT_DIR` with the proper directory):

```
0 2 * * * $GALAXY_ROOT_DIR/galaxy_cleanup.sh
```

This will clean up any **deleted** files every day at 2:00 am.  Log files will be stored in `$GALAXY_ROOT_DIR/galaxy_cleanup.log`.

For more information please see the [Purging Histories and Datasets][] document.  ***Note: the metadata about each analysis will still be stored and available in Galaxy, but the data file contents will be permanently removed.***

[Galaxy]: https://wiki.galaxyproject.org/FrontPage
[irida-galaxy.jpg]: images/irida-galaxy.jpg
[Galaxy API]: https://wiki.galaxyproject.org/Learn/API
[GetGalaxy]: https://wiki.galaxyproject.org/Admin/GetGalaxy
[Galaxy Cluster Setup]: cluster/
[Galaxy Environment Setup]: environment/
[Running Galaxy in a production environment]: https://wiki.galaxyproject.org/Admin/Config/Performance/ProductionServer
[Galaxy Disable Developer Settings]: https://wiki.galaxyproject.org/Admin/Config/Performance/ProductionServer#Disable_the_developer_settings
[Galaxy Database Setup]: https://wiki.galaxyproject.org/Admin/Config/Performance/ProductionServer#Switching_to_a_database_server
[MySQL]: http://www.mysql.com/
[PostgreSQL]: http://www.postgresql.org/
[Galaxy nginx setup]: https://wiki.galaxyproject.org/Admin/Config/nginxProxy
[Galaxy Cluster]: https://wiki.galaxyproject.org/Admin/Config/Performance/Cluster
[Galaxy News Page]: https://wiki.galaxyproject.org/News
[Bitbucket]: https://bitbucket.org/galaxy/galaxy-dist/commits/all
[IRIDA Toolshed]: https://irida.corefacility.ca/galaxy-shed
[Main Galaxy Toolshed]: https://toolshed.g2.bx.psu.edu/
[Galaxy Toolshed]: https://wiki.galaxyproject.org/ToolShed
[Galaxy Toolsheds]: https://wiki.galaxyproject.org/ToolShed
[Installing Repositories to Galaxy]: https://wiki.galaxyproject.org/InstallingRepositoriesToGalaxy
[SNVPhyl Whole Genome Phylogeny]: pipelines/phylogenomics/
[Assembly and Annotation]: pipelines/assembly-annotation/
[Galaxy Job Config]: https://wiki.galaxyproject.org/Admin/Config/Jobs
[saved-histories.jpg]: images/saved-histories.jpg
[scripts/galaxy]: scripts/galaxy
[galaxy-installed-repositories.jpg]: images/galaxy-installed-repositories.jpg
[history-options-icon]: images/history-options-icon.jpg
[Purging Histories and Datasets]: https://wiki.galaxyproject.org/Admin/Config/Performance/Purge%20Histories%20and%20Datasets
[Mercurial]: http://mercurial.selenic.com/
