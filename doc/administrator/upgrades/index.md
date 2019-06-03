---
layout: default
search_title: "Upgrades"
description: "Upgrade Notes"
---

# Upgrade Notes
{:.no_toc}
* This comment becomes a toc.
{:toc}

The majority of IRIDA's upgrade notes can be seen at <https://github.com/phac-nml/irida/blob/master/UPGRADING.md>.  When there are more major upgrades to the IRIDA system, database, deployment, etc. this page will go further in depth about how to properly back up your system, perform the upgrade, and recover from problems.

# 19.05
## FastQC translation to filesystem

This upgrade was built to greatly reduce the size of IRIDA's database (and database backups).  Before this upgrade, any time FastQC was run the output files were saved in the database `analysis_fastqc` table as `longblob` objects.  This worked fine when IRIDA had a smaller number of files, but as some installations are growing larger this is increasing the database size at an alarming rate.  The upgrade will move the FastQC images out of the database and store them in the configured `output.file.base.directory` alongside the other analysis output files.

We do not expect issues with this upgrade, but out of an abundance of caution recovery notes are listed below.

### Precautions to take before undertaking this update
{:.no_toc}

* Back up your database.  The FastQC images will be permanently removed from the database during this update so a backup should be performed in case anything goes wrong.
* The update will add a large number of small image files to your `output.file.base.directory`.  For each sequence file in your database, 3 files totaling approximately 50KB will be created.  Ensure your filesystem will be able to handle this increase in files.
* If you want to be extra cautious, you can backup your configured `output.file.base.directory`.

### Performing the upgrade
{:.no_toc}

This upgrade will proceed as a normal database upgrade handled by Liquibase.  You shouldn't need to do anything special, just ensure you've taken your database backups.

### In case of an error
{:.no_toc}

First step in case of an error should always be to stop Tomcat.

This database upgrade works in 2 stages:

1. Copy all the FastQC images into a temporary directory within your `output.file.base.directory`.
2. Move all the images from the temporary directory to their final resting spot inside `output.file.base.directory`.

What to do for recovery if something goes wrong will be different depending on the stage.

#### Stage 1 - Creating temporary files
{:.no_toc}

If a problem occurs during stage 1 you're probably going to see the following error message:

> There was a problem moving the FastQC images from the database to the filesystem.  The directory (**YOUR TEMP DIRECTORY**) contains the temporary FastQC images that should have been moved to the analysis output directory.  In order to re-apply this update, please restore the database from a backup, and then re-start IRIDA.  You do *not* have to cleanup existing FastQC images on the filesystem as they will be re-written.  Once your upgrade has completed successfully, you can safely remove the temp directory.

As the error message states, this means there was a problem while creating the temporary files.  If you look inside (**YOUR TEMP DIRECTORY**) you should see a directory with lots of numbered subdirectories with some `.png` files with the FastQC images inside.  To recover from this point:

1. Restore your database backup
2. Remove the temporary directory created during the upgrade.  It will be included in the error message.
3. Retry the upgrade by starting Tomcat again.

#### Stage 2 - Moving files to output directory
{:.no_toc}

Issues with this step will be much more rare, but If a problem occurs here it's a bit more of a process to clean up.  You'll see the following message:

> There was a problem moving the FastQC images from the temporary file location (**YOUR TEMP DIRECTORY**) to their final output file location.  This is going to involve some manual cleanup.  We recommend first creating a backup of (**YOUR OUTPUT DIRECTORY**).  The previous max file ID was (**PREVIOUS MAX ID**).  Any files in (**YOUR OUTPUT DIRECTORY**) with an ID greater than (**PREVIOUS MAX ID**) were created during this process and can be deleted.  The directory (**YOUR TEMP DIRECTORY**) contains the temporary FastQC images that should have been moved to the analysis output directory.  In order to re-apply this update, please restore the database from a backup, and then re-start IRIDA.  Once your upgrade has completed successfully, you can safely remove the temp directory.

What's happened at this point is an error occurred while moving the temporary files to their output location.  As stated in the message above you'll need to manually delete some files to clean up from here.

1. (optional but recommended) Back up your analysis output files directory `output.file.base.directory`.
2. Restore your database backup.
3. Check the error message.  It will note the pre-upgrade largest output file id "The previous max file ID was (**PREVIOUS MAX ID**)".
4. Remove all directories from your `output.file.base.directory` with an ID **GREATER THAN** the above noted ID.  These files were created as part of the upgrade process.
5. Retry the upgrade by starting Tomcat again.
6. Remove the temporary directory created during the failed upgrade.  It will be included in the error message. "... temporary file location (**YOUR TEMP DIRECTORY**)".

#### Database timeout error

If you have lots of sequence files stored in IRIDA, this upgrade can take a while to write all the FastQC results to the filesystem. This could lead to database connection timeout issues (the default timeout is 8 hours for MySQL/MariaDB). You should see a message in your log like below if this error were to occur:

```
Error: The last packet successfully received from the server was 38,881,098 milliseconds ago.  The last packet sent successfully to the server was 38,885,372 milliseconds ago. is longer than the server configured value of 'wait_timeout'. You should consider either expiring and/or testing connection validity before use in your application, increasing the server configured values for client timeouts, or using the Connector/J connection property 'autoReconnect=true' to avoid this problem. [Failed SQL: ALTER TABLE irida_prod_test.analysis_output_file MODIFY file_path VARCHAR(255) NOT NULL]
```

If you encounter this error, you can increase the database `wait_timeout` value by modifying the database connection string in `/etc/irida/irida.conf` by appending `?sessionVariables=wait_timeout=57600` like below:

```
jdbc.url=jdbc:mysql://localhost:3306/irida_prod_test?sessionVariables=wait_timeout=57600
```

Modify `57600` to some appropriate timeout value (in seconds). Once you've made this modification, you will have to go through the instructions above on recovering from an error before restarting the upgrade. Once the upgrade is complete, you can remove this variable from the connection string and restart IRIDA to reset to the default database timeout value.
