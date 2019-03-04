Upgrading
=========

This document summarizes the environmental changes that need to be made when
upgrading IRIDA that cannot be automated.

19.01 to 19.04
--------------

0.22.0 to 19.01
----------------
* The following new Tomcat variable should be set for deployment `irida.db.profile=prod` for production deployments. See https://irida.corefacility.ca/documentation/administrator/web/#servlet-container-configuration for more details.
* A new configuration value is avaliable to display a warning on analysis result and metadata pages to communicate that an analysis result should be considered preliminiary.  Add a warning message `irida.analysis.warning` in `/etc/irida/web.conf` to display on all analysis result and metadata pages.
* New Spring profiles are available for running IRIDA in a multi-server mode.  This will help distribute the load in high-usage installations.  See the documentation for more details at https://irida.corefacility.ca/documentation/administrator/web/#multi-web-server-configuration.
* The [AssemblyAnnotation](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/assembly-annotation/) and [AssemblyAnnotationCollection](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/assembly-annotation-collection/) pipelines have been upgraded to make use of [shovill](https://github.com/tseemann/shovill) for assembly and [QUAST](http://quast.sourceforge.net/quast.html) for assembly quality assessment. Please ensure that the `shovill` and `quast` Galaxy tools are installed for these pipelines. If you haven't already, please follow [the instructions for installing `shovill`](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/assembly-annotation/#address-shovill-related-issues) and see the instructions for upgrading the [SISTR](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/sistr/) pipeline from 0.21.0 to 0.22.0 for more info.
* A method of installing pipeline plugins has been included in this release of IRIDA. These are distributed as independent JAR files. If you wish to install a plugin, please copy the JAR file to `/etc/irida/plugins` and restart IRIDA. See <https://irida.corefacility.ca/documentation/developer/tools/pipelines/> and <https://github.com/phac-nml/irida-plugin-example> for details on constructing plugins.

0.21.0 to 0.22.0
----------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* This upgrade changes the way the file processors handle uploaded files.  File processing now takes place as a scheduled task rather than immediately after files are uploaded.  For deployments with multiple IRIDA servers running against the same database, prossing may not be performed by the IRIDA server the files were uploaded to and will instead be balanced among all the available servers.  If you want to disable file processing on an IRIDA server, set the following property in `/etc/irida/irida.conf` : `file.processing.process=false`.
* A new pipeline, [bio_hansel](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/bio_hansel/), has been included. You will have to make sure to install the necessary Galaxy tools listed in the documentation.
* The [MentaLiST](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/mentalist/) pipeline has been ugpraded. Please make sure to install the necessary tools in Galaxy.
* The [SISTR](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/sistr/) pipeline has been upgraded to make use of [shovill](https://github.com/tseemann/shovill) for assembly. Please make sure to install the `shovill` Galaxy tool. Also, please make sure to follow the additional instructions in <https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/sistr/#address-shovill-related-issues>, which involves some modifications of the conda environment for `shovill`. In particular, you must:

    1. Install the proper `ncurses` and `bzip2` packages from the **conda-forge** channel.

        ```bash
        # activate the Galaxy shovill conda env
        source galaxy/deps/_conda/bin/activate galaxy/deps/_conda/envs/__shovill@0.9.0
        # install ncurses and bzip2 from conda-forge channel
        conda install -c conda-forge ncurses bzip2
        ```

    2. Set the `SHOVILL_RAM` environment variable in the conda environment:

        ```bash
        cd galaxy/deps/_conda/envs/__shovill@0.9.0
        mkdir -p etc/conda/activate.d
        mkdir -p etc/conda/deactivate.d

        echo -e "export _OLD_SHOVILL_RAM=\$SHOVILL_RAM\nexport SHOVILL_RAM=8" >> etc/conda/activate.d/shovill-ram.sh
        echo -e "export SHOVILL_RAM=\$_OLD_SHOVILL_RAM" >> etc/conda/deactivate.d/shovill-ram.sh
        ```

        Please change `8`GB to what works for you for `shovill` (or setup based on the `$GALAXY_MEMORY_MB` environment variable, see the linked instructions for more details).

0.20.0 to 0.21.0
----------------

* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* Two new pipelines, [refseq_masher](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/refseq_masher) and [MentaLiST](https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/mentalist), are included with this release.  Additional Galaxy tools will need to be installed.  Please see the linked installation details for each pipeline for more information.

0.19.0 to 0.20.0
----------------

* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* This upgrade removes Dandelion framework from IRIDA.  `-Ddandelion.profile.active=prod"` should be removed from Tomcat settings.  Please see <https://irida.corefacility.ca/documentation/administrator/web/#servlet-container-configuration>.
* You may configure the number of days a password is valid for before it needs reset.  Add the `security.password.expiry` key to `/etc/irida/irida.conf` to configure.  To disable password expiry, set to `-1` (default).  For example to set to 90 days, add the following:

```
security.password.expiry=90
```

0.18.0 to 0.19.0
----------------

* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* Any custom pipelines developed should follow the new `Analysis` scheme.  An `Analysis` subclass is no longer required (but may be used) for custom pipelines, but an `AnalysisType` entry is required.  See developer docs for more information.


0.17.0 to 0.18.0
----------------

* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.

0.16.0 to 0.17.0
----------------

* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.  Note this upgrade removes a number of deprecated database tables such as `remote_related_proejct` and `remote_sequence_file`.  While these tables were likely empty it is **strongly** recommended you back up your database before this upgrade.
* Remove `snapshot.file.base.directory` entry from `/etc/irida/irida.conf`.  This directory is no longer used.  If the directory pointed to by this config entry is empty it may be safely removed.
* The SISTR pipeline has been upgraded to use `sistr_cmd` 1.0.2.  This new version must be installed in Galaxy.  Please see <http://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/sistr/>.

0.15.0 to 0.16.0
----------------

* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* A new pipeline (SISTR) has been added and requires installation of additional tools and dependencies in Galaxy.  Please see <http://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/sistr/>.
* The **SPAdes** tool has been updated to <https://toolshed.g2.bx.psu.edu/view/nml/spades/35cb17bd8bf9>, please update this tool in Galaxy. See <http://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/assembly-annotation/> for more details on upgrading.

0.14.0 to 0.15.0
----------------
* A new version of SNVPhyl should be installed in Galaxy (version 1.0.1).  You must install the repository `suite_snvphyl_1_0_1` with revision `4841b7148c44` from the [IRIDA Main Toolshed](https://irida.corefacility.ca/galaxy-shed/view/nml/suite_snvphyl_1_0_1/4841b7148c44).  Please see <https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/phylogenomics/#irida-whole-genome-phylogenomics> for more information.
* Enable Dandelion `prod` profile by setting the environment variable `dandelion.profile.active=prod` in the Tomcat settings.  See https://irida.corefacility.ca/documentation/administrator/web/#servlet-container-configuration for more information.
* This upgrade adds a configuration value to limit the number of running workflows IRIDA will submit to Galaxy.  You can set `irida.workflow.max-running` in `/etc/irida/irida.conf` to change this value.  Default value is `4`.
* This upgrade adds a configuration value to allow multiple threads to upload files to Galaxy.  You can set `galaxy.library.upload.threads` in `/etc/irida/irida.conf`.  Default value is `1`.
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.

0.13.0 to 0.14.0
----------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.

0.12.0 to 0.13.0
----------------
* You may now configure the number of threads to be used for file processing on the web server.  These threads perform tasks such as unzipping files and running FastQC.  The following configuration keys can be set in `/etc/irida/irida.conf` (default values displayed):

        file.processing.core.size=4
        file.processing.max.size=8
        file.processing.queue.capacity=512
* Deploy the new `war` file.

0.11.0 to 0.12.0
----------------
* Make sure that the path prefixes in `/etc/irida/irida.conf` match what's in the database so that the relative path transformation works correctly.
* A new version of SNVPhyl should be installed in Galaxy (version 1.0).  You must install the repository `suite_snvphyl_1_0_0` with revision `4e41de612a14` from the [IRIDA Main Toolshed](https://irida.corefacility.ca/galaxy-shed/view/nml/suite_snvphyl_1_0_0/4e41de612a14).  Please see <https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/phylogenomics/#irida-whole-genome-phylogenomics> for more information.
* Deploy the new `war` file.

1.0.0 to 0.11.0
---------------
* No special upgrade steps required, just deploy the new `war` file.

1.0.0-alpha10 to 1.0.0
----------------------
* You may now configure the link and text that gets rendered on web pages under the 'Help' menu for accessing an external help forum. You can configure the link and text by adding some keys to `/etc/irida/web.conf`:
        
        help.page.title=Your Help Page Title
        help.page.url=http://www.example.org/help
  These are optional settings. If they are not configured, no link will appear in the 'Help' menu.
* You may now configure the e-mail address that gets rendered on web pages under the 'Help' menu. You can configure the e-mail address by adding a key to `/etc/irida/web.conf`:
       
        help.contact.email=you@example.org
  This is an optional setting. If it is not configured, no 'Contact Us' e-mail address will appear in the 'Help' menu.

* Deploy the new `war` file.   

1.0.0-alpha9 to 1.0.0-alpha10
-----------------------------
* No special upgrade steps required, just deploy the new `war` file.

1.0.0-alpha8 to 1.0.0-alpha9
----------------------------
* No special upgrade steps required, just deploy the new `war` file.

1.0.0-alpha8 to 1.0.0-alpha8.2
----------------------------
* Add `irida.scheduled.threads` key to `/etc/irida/irida.conf` file with the size of your desired thread pool.  Suggested size is `2`.

1.0.0-alpha7 to 1.0.0-alpha8
----------------------------
* (Optional) Add new keys for exporting data to NCBI's SRA. Please see https://irida.corefacility.ca/documentation/administrator/web/#core-configuration for a complete list of the keys to add (all use the prefix `ncbi.upload`). You must create an account with NCBI to enable this feature: http://www.ncbi.nlm.nih.gov/books/NBK47529/#_SRA_Quick_Sub_BK_Establishing_a_Center_A_

1.0.0-alpha6 to 1.0.0-alpha7
----------------------------

* A new version of SNVPhyl should be installed in Galaxy (version 0.3).  You must install the repository `suite_snvphyl_0_3_0` with revision `bb2e651149da` from the [IRIDA Main Toolshed](https://irida.corefacility.ca/galaxy-shed/view/nml/suite_snvphyl_0_3_0/bb2e651149da).  Please see https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/phylogenomics/#irida-whole-genome-phylogenomics for more information.
* A new version of Prokka should be installed in Galaxy (version 1.11.0).  You must install the repository `prokka` with revision `f5e44aad6498` from the [Galaxy Main Toolshed](https://toolshed.g2.bx.psu.edu/view/crs4/prokka/f5e44aad6498).  Please see https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/assembly-annotation/ for more information.

1.0.0-alpha5 to 1.0.0-alpha6
----------------------------
* No special upgrade steps required, just deploy the new `war` file.

1.0.0-alpha4 to 1.0.0-alpha5
----------------------------
* A new version of SNVPhyl should be installed in Galaxy, you must install a new tool repository `suite_snvphyl` with revision `99463e5aef1b` from the IRIDA Main Toolshed. Please see https://irida.corefacility.ca/documentation/administrator/galaxy/pipelines/phylogenomics/#irida-whole-genome-phylogenomics for more information.
* You can configure the location of the announcements file by adding a key to `/etc/irida/irida.conf` with the name `updates.file` with a value of the location of the Markdown formatted file that will be displayed. The user running your serlvet container must be able to read this file. Example:

        updates.file=/etc/irida/announcments.md

* Administrator notifications for filesystem errors during file uploads in the web interface can be configured by adding a key to `/etc/irida/irida.conf` with the name `irida.administrative.notifications.email` with a value of the e-mail address to which notifications can be sent. Notifications are sent when the IRIDA-managed filesystem is in an inconsistent state (trying to write over files that already exist), when there is no disk space left on the volume to which the files are written, or when some other, unknown filesystem-related exception takes place. Example:

        irida.administrative.notifications.email=admin_user@irida.ca

* The schedule for sending out project digest e-mails is defaulted to be once per day at midnight. You can customize the frequency for the digest notifications by adding a key to `/etc/irida/irida.conf` with the name `irida.scheduled.subscription.cron` with a value of a cron-like expression. Please see http://docs.spring.io/spring/docs/4.0.6.RELEASE/javadoc-api/org/springframework/scheduling/annotation/Scheduled.html#cron-- for the format of the expression. Example:

        # send e-mails weekly (on Sunday at midnight) instead of daily
        irida.scheduled.subscription.cron=0 0 0 * * 0

1.0.0-alpha3 to 1.0.0-alpha4
----------------------------
* No special upgrade steps required, just deploy the new `war` file.
