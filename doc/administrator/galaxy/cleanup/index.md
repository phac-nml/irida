---
layout: default
search_title: "Automated Cleanup of Galaxy files"
description: "Automated Cleanup of Galaxy files"
---

# Automated Galaxy Data Cleanup

IRIDA stores and manages both the input files to an analysis workflow as well as the output files and provenance information from a workflow run through Galaxy.  In the process of running an analysis, many intermediate files are produced by Galaxy (SAM/BAM files, log files, etc), as well as intermediate data structures (Galaxy Data Libraries for storing input files to Galaxy, and the workflow uploaded to Galaxy).  These additional files and data structures are not stored or used by IRIDA following the completion of an analysis.

By default IRIDA will **not** remove any of the data generated and stored in Galaxy.  This provides additional resources beyond the output files and provenance information stored by IRIDA for each analysis.

However, some of the files produced by Galaxy can be quite large and may quickly fill up the storage capacity of the Galaxy server.  IRIDA can be instructed to clean up this data after a period of time by adjusting the parameter `irida.analysis.cleanup.days` in the main IRIDA configuration file `/etc/irida/irida.conf`.  This controls the number of days before IRIDA will remove analysis files from Galaxy.  This can be used to reduce the storage requirements for each analysis at the expense of not having any intermediate analysis files available.

Once the parameter `irida.analysis.cleanup.days` is set, IRIDA will periodically (once every hour) check for any analyses that have expired and clean up the necessary files in Galaxy.  However, these files will only be marked as **deleted** in Galaxy, not permanently removed.  To permanently remove these files, please do the following:

## Step 1: Create a Galaxy Cleanup script

The following is an example script that can be used to clean up **deleted** files in Galaxy.  Please save this script to `galaxy/galaxy_cleanup.sh`, make executable with `chmod u+x galaxy/galaxy_cleanup.sh`, and then make any necessary modifications to the variables.  In particular, please set `$GALAXY_ROOT_DIR` and `$CONDA_ROOT` to point to the `galaxy/` directory, and the `~/miniconda3` directory. We assume the conda installation has an environment **galaxy** containing dependencies needed for starting Galaxy.  Modify this as appropriate (e.g., if you use a Galaxy virtual environment instead).

```bash
#!/bin/bash

GALAXY_ROOT_DIR=/path/to/galaxy-dist
CLEANUP_LOG=$GALAXY_ROOT_DIR/galaxy_cleanup.log
CONDA_ROOT=/path/to/conda/for/galaxy

source $CONDA_ROOT/bin/activate galaxy

cd $GALAXY_ROOT_DIR

echo -e "\nBegin cleanup at `date`" >> $CLEANUP_LOG
echo -e "Individual log files in scripts/cleanup_datasets/*.log" >> $CLEANUP_LOG

echo -e "Begin delete userless histories" >> $CLEANUP_LOG
sh scripts/cleanup_datasets/delete_userless_histories.sh 2>&1 >> $CLEANUP_LOG

echo -e "\nBegin purge deleted histories" >> $CLEANUP_LOG
sh scripts/cleanup_datasets/purge_histories.sh 2>&1 >> $CLEANUP_LOG

echo -e "\nBegin purge deleted libraries" >> $CLEANUP_LOG
sh scripts/cleanup_datasets/purge_libraries.sh 2>&1 >> $CLEANUP_LOG

echo -e "\nBegin purge deleted folders" >> $CLEANUP_LOG
sh scripts/cleanup_datasets/purge_folders.sh 2>&1 >> $CLEANUP_LOG

echo -e "\nBegin delete datasets" >> $CLEANUP_LOG
sh scripts/cleanup_datasets/delete_datasets.sh 2>&1 >> $CLEANUP_LOG

echo -e "\nBegin purge deleted datasets" >> $CLEANUP_LOG
sh scripts/cleanup_datasets/purge_datasets.sh 2>&1 >> $CLEANUP_LOG

echo -e "\nEnd cleanup at `date`" >> $CLEANUP_LOG
```

The particular cleanup scripts (e.g., `scripts/cleanup_datasets/delete_userless_histories.sh`) default to removing only items > 10 days old. If you wish to adjust this time, please modify the `-d 10` parameter within these scripts.

## Step 2: Schedule script to run using cron

Once this script is installed, it can be scheduled to run periodically by adding a cron job for the Galaxy user.  To do this, please run `crontab -e` and past the following line (replacing `galaxy/` with the proper directory):

```
0 2 * * * galaxy/galaxy_cleanup.sh
```

This will clean up any **deleted** files every day at 2:00 am.  Log files will be stored in `galaxy/galaxy_cleanup.log` and `galaxy/cleanup_datasets/*.log`.

For more information please see the [Purging Histories and Datasets](https://galaxyproject.org/admin/config/performance/purge-histories-and-datasets/) document.  ***Note: the metadata about each analysis will still be stored and available in Galaxy, but the data file contents will be permanently removed.***

# Cleaning up temporary files

When using Galaxy with an IRIDA instance which is using cloud based storage (Azure, AWS, etc) for example, files are uploaded from IRIDA instead of linking to them since the files are stored in the cloud and not on a shared filesystem. Since these files are uploaded to Galaxy it is a good idea to clean these files up. An example script that can be used to clean these files up is provided below:

```bash
#!/bin/bash

GALAXY_ROOT_DIR=/path/to/galaxy-dist
CLEANUP_LOG=$GALAXY_ROOT_DIR/irida_galaxy_tmp_files_cleanup.log
TMP_FILES_DIR=$GALAXY_ROOT_DIR/databases/tmp/
NUMBER_OF_DAYS_OLD=30

source $CONDA_ROOT/bin/activate galaxy

echo -e "\nBegin temporary file cleanup at `date`" >> $CLEANUP_LOG
find $TMP_FILES_DIR -mindepth 1 -mtime +$NUMBER_OF_DAYS_OLD -delete

echo -e "\nEnd temporary file cleanup at `date`" >> $CLEANUP_LOG
```

This can be added as a cleanup script which can be scheduled to run using cron.