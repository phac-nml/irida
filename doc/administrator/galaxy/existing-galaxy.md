---
layout: default
search_title: "Integration with an existing Galaxy"
description: "Integration with an existing Galaxy"
---

Integration with an existing Galaxy
===================================
{:.no_toc}

IRIDA can be setup to use an existing Galaxy installation assuming a few conditions are met:

1. Galaxy version >= **16.01** is required as IRIDA makes use of [conda with Galaxy][].  Earlier versions were supported previously, but are being phased out as more required tools are released under conda.
2. The filesystem is shared between the machines serving IRIDA and Galaxy under the same paths (e.g., `/path/to/irida-data` on IRIDA is available as `/path/to/irida-data` on the Galaxy server).
3. Galaxy is setup to use [PostgreSQL][] or [MySQL/MariaDB][] as it's database.  The default installation uses SQLite, but this is insufficient for the complex workflows used by IRIDA.
4. Some modifications to the configuration settings (see below) are made to enable IRIDA to communicate with Galaxy.

The following describes the procedures needed to get IRIDA setup with an existing Galaxy installation.

* This comment becomes the table of contents.
{:toc}

Setup of a Galaxy user and API key
----------------------------------

As IRIDA communicates with Galaxy through the API it is necessary to setup a Galaxy API key which will be used by IRIDA. It is recommended (though not required) to use a separate user for this purpose.

This user must also have Galaxy admin privileges in order to allow IRIDA to link directly to the sequence files when sharing with Galaxy (avoids creating copies of fastq files each time a workflow is run). Admin privileges can be assigned by modifying the Galaxy configuration file `config/galaxy.ini` and adding the user's email to `admin_users`.

Other configuration settings
----------------------------

### Settings in `config/galaxy.ini`

The following is a list of other necessary configuration settings within the file `config/galaxy.ini` for IRIDA to function with Galaxy.

1. Change `allow_library_path_paste` to allow direct linking of files in Galaxy to the IRIDA file locations. E.g.,
   * Change `#allow_library_path_paste = False` to `allow_library_path_paste = True`.
2. Set the Galaxy `id_secret` for encoding database ids. E.g.,
   * Change `#id_secret = USING THE DEFAULT IS NOT SECURE!` to `id_secret = some secure password`
      * The command `pwgen --secure -N 1 56` may be useful for picking a hard-to-guess key.
      * ***Note: Once this key is set, please do not change it.  This key is used to translate database ids in Galaxy to API ids used by IRIDA to access datasets, histories, and workflows.  IRIDA does store some of these API ids internally for debugging and tracking purposes and changing this value will render any of the API ids stored in IRIDA useless.***
3. Setup [Conda][] for installing tool dependencies. E.g.,
   * Set `conda_prefix = /home/galaxy-irida/miniconda3`, or wherever conda is installed for Galaxy.
   * Set `conda_ensure_channels = iuc,bioconda,r,defaults,conda-forge`.

### Build dependencies

Even by defaulting to using conda, some tools will need to be built from source, and so require the standard Linux build environment to be installed. For CentOS this will include the following packages:

```bash
yum groupinstall "Development tools"
yum install mercurial zlib-devel ncurses-devel tcsh git db4-devel expat-devel java
```

Required tools in Galaxy
------------------------

### Step 1: Configure External Toolsheds

The workflows used by IRIDA make use of external tools that can be installed using a [Galaxy Toolshed][].  The two toolsheds used by IRIDA are the [Main Galaxy Toolshed][] and the [IRIDA Toolshed][].  These are configured in the file `config/tool_sheds_conf.xml`.  Please make sure the following two `tool_shed` entries are setup within this file:

```xml
<tool_sheds>
	<tool_shed name="Galaxy main tool shed" url="http://toolshed.g2.bx.psu.edu/"/>
	<tool_shed name="IRIDA Galaxy Toolshed" url="https://irida.corefacility.ca/galaxy-shed"/>
</tool_sheds>
```

### Step 2: Install Pipeline Tools

#### Automated installation of tools

An automated script (`install_tool_shed_tools.py`) to install all necessary tools for the different pipelines to run in Galaxy is provided with the `irida-[version].zip` download. This can be found on the [IRIDA releases][] page.  Instructions can be accessed at [Automated tools install][].

To run this script, please do the following:

```
# Installs dependency modules for script
pip install -r install-tools-requirements.txt

# Do installation of Galaxy tools
python install_tool_shed_tools.py --toolsfile tools-list.yml --galaxy [http://url-to-galaxy] --apikey [api key]
```

You may want to monitor the Galaxy log file as the installation is proceeding.  This may take a while to download, build, and install all tools.

#### Manual installation of tools

Alternatively, the necessary tools can be installed manually through the following instructions specific to each pipeline in IRIDA:

* [SNVPhyl Whole Genome Phylogeny][]
* [Assembly and Annotation][]
* [Assembly and Annotation Collection][]
* [SISTR Salmonella Typing][]

Link up Galaxy with IRIDA
-------------------------

In order to connect IRIDA to this Galaxy instance you will need to modify the parameters **galaxy.execution.url**, **galaxy.execution.email**, and **galaxy.execution.apikey** in the file `/etc/irida/irida.conf` and restart IRIDA. Additional details for configuring IRIDA can be found in instructions to [install and configure the IRIDA web interface](../web).

Once you have configured IRIDA to connect to Galaxy you can attempt to execute a workflow by adding some data to your cart, selecting  **Pipelines** from the main menu, then selecting a particular pipeline.  You will have to have some data uploaded into IRIDA before testing.  An example set of data can be found at [irida-sample-data.zip][].  Currently all workflows assume you are using paired-end sequence reads.

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

[conda with Galaxy]: https://docs.galaxyproject.org/en/master/admin/conda_faq.html
[Conda]: https://conda.io/miniconda.html
[PostgreSQL]: http://www.postgresql.org/
[IRIDA Toolshed]: https://irida.corefacility.ca/galaxy-shed
[Main Galaxy Toolshed]: https://toolshed.g2.bx.psu.edu/
[Galaxy Toolshed]: https://wiki.galaxyproject.org/ToolShed
[SNVPhyl Whole Genome Phylogeny]: pipelines/phylogenomics/
[SISTR Salmonella Typing]: pipelines/sistr/
[Assembly and Annotation]: pipelines/assembly-annotation/
[Assembly and Annotation Collection]: pipelines/assembly-annotation-collection/
[Purging Histories and Datasets]: https://wiki.galaxyproject.org/Admin/Config/Performance/Purge%20Histories%20and%20Datasets
[Automated tools install]: https://github.com/phac-nml/irida/tree/development/packaging#automated-processupgrading
[IRIDA releases]: https://github.com/phac-nml/irida/releases
[PostgreSQL]: https://www.postgresql.org/
[MySQL/MariaDB]: https://mariadb.org/
[irida-sample-data.zip]: https://irida.corefacility.ca/downloads/data/irida-sample-data.zip
