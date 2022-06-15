Upgrading
=========

This document summarizes the environmental changes that need to be made when
upgrading IRIDA that cannot be automated.

22.03 to 22.05
--------------
* This upgrade deprecates two pipelines, SISTR_TYPING and MLST_MENTALIST, and disables them from being executed. Any previously-run analysis results will still function as normal. If you wish to re-enable these pipelines you can set `irida.workflow.types.disabled=` (i.e., set the value to empty) in the `/etc/irida/irida.conf` file and restart IRIDA. If you wish to keep these pipelines disabled but include your own additional disabled pipelines you can set `irida.workflow.types.disabled=SISTR_TYPING,MLST_MENTALIST` and add your own pipelines to disable after this list.
   * For SISTR_TYPING, it is recommended to switch to using the version implemented as a plugin <https://github.com/phac-nml/irida-plugin-sistr>. For MLST_MENTALIST, we can no longer provide support for the installation of this software in IRIDA and Galaxy and have chosen to deprecate it.

22.01 to 22.03
--------------
* It is recommended to stop the servlet container before deploying the new `war` file.

21.09 to 22.01
--------------
* This upgrade converted the project from bare Spring to Spring Boot, which deprecated a number of properties relating to database connection and setup. These deprecated properties are mentioned in [/etc/irida/irida.conf](https://phac-nml.github.io/irida-documentation/administrator/web/#core-configuration).
* Due to an update in Spring you will need to revoke tokens for all OAuth clients, you can perform this through the UI or with the following sql:
```sql
USE IRIDA_DB_NAME;
truncate oauth_access_token;
truncate oauth_refresh_token;
```
* This upgrade changed the way css and js assets are compiled and as such custom login pages will need to be updated.
  * Replace `<link rel="stylesheet" th:href="@{/dist/css/login.bundle.css}" />` with `<webpacker:css entry="login" />`.
  * Replace `<script th:src="@{/dist/js/login.bundle.js}"></script>` with `<webpacker:js entry="login" />`.

21.09.1 to 21.09.2
------------------
* This updates log4j from `1.2.17` to `2.17.0`. Most users will not need to take any action, but log4j 2 uses a different (XML) syntax for configuration. If you have any custom log4j configurations these will need to be updated to match the [log4j 2 syntax](https://logging.apache.org/log4j/2.x/manual/configuration.html).
   * *Note: 1.X is **not** vulnerable to [log4shell](https://cve.report/CVE-2021-44228) but it is no longer supported and there are other issues with 1.X which is why we are upgrading as a hotfix. See also <https://www.slf4j.org/log4shell.html>.*

21.05 to 21.09
--------------
* It is recommended to stop the servlet container before deploying the new `war` file.

21.05.1 to 21.05.2
------------------
* The [SNVPhyl](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/phylogenomics/) pipeline has been upgraded to version `1.2.3`. Please make sure to install the necessary tools in Galaxy.

21.01.3 to 21.05
--------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.

21.01 to 21.01.3
--------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.

20.09 to 21.01
--------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* IRIDA 21.01 includes changes to the analysis plugin system.  Analysis pipelines must be updated to support this new change.
* This upgrade includes changes to the sample-metadata database system. While implementing this change we encountered
  issues with the metadata auditing system, so we recommend additional backup steps before performing this upgrade.
  See <https://phac-nml.github.io/irida-documentation/administrator/upgrades/#sample-metadata-audit-record-updates> for
  more details.
* A couple of new columns have been added to the announcements' table in the database. The new announcement title column
  cannot be empty and will be given a default value. There is a script available to populate the new title from the
  header of the existing announcements content, which can be found under the `src/main/resources/scripts/announcements`
  folder in the IRIDA repo.

20.05 to 20.09
--------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* The [SNVPhyl](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/phylogenomics/) pipeline has been upgraded. Please make sure to install the necessary tools in Galaxy.
* The [AssemblyAnnotation](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/assembly-annotation/) and [AssemblyAnnotationCollection](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/assembly-annotation-collection/) pipelines have been upgraded. Please make sure to install the necessary tools in Galaxy.
* We have upgraded our Galaxy Docker image to Galaxy 20.05: `docker pull phacnml/galaxy-irida-20.05`. The default account credentials for this Galaxy instance have changed. The username remains as `admin@galaxy.org` but the password is `password`. The admin key has also changed to `fakekey`. Please keep these changes in mind (and make adjustments to the Galaxy credentials in `/etc/irida/irida.conf`) if you use the Docker image.
    * We were also unable to automatically install the MentaLiST tool when building the Docker image. It does seem to be working when installing via the Galaxy web UI (though you may have to attempt to reinstall it multiple times), so if you wish to use MentaLiST in the Galaxy Docker instance you will have to install the tool once the Docker container is started. Please see the [MentaLiST install instructions](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/mentalist/) for more details. Once installed you will have to run `docker exec -it [CONTAINER ID] supervisorctl restart galaxy:` to restart Galaxy before you can use the tool.
* While testing out tool installation we were encountering some issues installing tools via the web UI for older versions of Galaxy (<20.01). If installation of the Galaxy tools is not working via the Galaxy UI for older Galaxy instances we recommend using the tool [Ephemeris](https://ephemeris.readthedocs.io) to install tools in Galaxy via the command-line. More information about using Ephemeris to install IRIDA tools can be found at <https://phac-nml.github.io/irida-documentation/administrator/galaxy/setup/#automated-installation-of-tools>.

20.01 to 20.05
--------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* This version changes the endpoint for creating sequencing runs to allow any type of sequencer.  The legacy `sequencingRun/miseq` endpoint is maintained, but deprecated.  See <https://phac-nml.github.io/irida-documentation/developer/rest/#creating-sequencing-runs> for more info.
* Assemblies can now be uploaded to IRIDA rather than just created through an analysis pipeline.  This requires a new filesystem directory configured for files to be stored.  You must add `assembly.file.base.directory` to your `/etc/irida/irida.conf` file and create a new diretory for these files to be stored.

19.09 to 20.01
--------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* This upgrade changes the Java version to Java 11.  To upgrade, follow the install instructions for your system in <https://phac-nml.github.io/irida-documentation/administrator/web/#prerequisite-install-instructions>.
* Tomcat 8 (or another Servlet 3.1 compatible servlet container) is required for this IRIDA version.  Systems using Tomcat 7 must be upgraded before deploying this update.
* This upgrade adds a required field to OAuth2 clients using the `authorization_code` grant (that is external applications connecting to IRIDA via the web application).  This includes other IRIDA installations synchronizing data via the Remote API system, and Galaxy importer clients.  In order for these systems to continue working properly, administrators must register a redirect URI for all `authorization_code` clients.  For more on this process, see <https://phac-nml.github.io/irida-documentation/administrator/upgrades/#2001>.
* The configuration key `hibernate.dialect=org.hibernate.dialect.MySQL55Dialect` should be set in your `/etc/irida/irida.conf` file for this release.  Note the change from `MySQL5Dialect` to `MySQL55Dialect`.

19.05 to 19.09
--------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.

19.01 to 19.05
--------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* This upgrade will remove FastQC resuts from the database and move them to the file system.  It is **strongly** recommended to make a backup of your database before this upgrade.  Before upgrading you should read more at https://phac-nml.github.io/irida-documentation/administrator/upgrades/#1905.

19.01 to 19.01.2
----------------
* A new configuration value is available to control the number of threads used for communication with Galaxy when running pipelines. The default value is **4**. To change, please set `irida.workflow.analysis.threads` in the `/etc/irida/irida.conf` file. This can help when running lots of pipelines in IRIDA.


0.22.0 to 19.01
----------------
* The following new Tomcat variable should be set for deployment `irida.db.profile=prod` for production deployments. See https://phac-nml.github.io/irida-documentation/administrator/web/#servlet-container-configuration for more details.
* A new configuration value is avaliable to display a warning on analysis result and metadata pages to communicate that an analysis result should be considered preliminiary.  Add a warning message `irida.analysis.warning` in `/etc/irida/web.conf` to display on all analysis result and metadata pages.
* New Spring profiles are available for running IRIDA in a multi-server mode.  This will help distribute the load in high-usage installations.  See the documentation for more details at https://phac-nml.github.io/irida-documentation/administrator/web/#multi-web-server-configuration.
* The [AssemblyAnnotation](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/assembly-annotation/) and [AssemblyAnnotationCollection](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/assembly-annotation-collection/) pipelines have been upgraded to make use of [shovill](https://github.com/tseemann/shovill) for assembly and [QUAST](http://quast.sourceforge.net/quast.html) for assembly quality assessment. Please ensure that the `shovill` and `quast` Galaxy tools are installed for these pipelines. If you haven't already, please follow [the instructions for installing `shovill`](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/assembly-annotation/#address-shovill-related-issues) and see the instructions for upgrading the [SISTR](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/sistr/) pipeline from 0.21.0 to 0.22.0 for more info.
* A method of installing pipeline plugins has been included in this release of IRIDA. These are distributed as independent JAR files. If you wish to install a plugin, please copy the JAR file to `/etc/irida/plugins` and restart IRIDA. See <https://phac-nml.github.io/irida-documentation/developer/tools/pipelines/> and <https://github.com/phac-nml/irida-plugin-example> for details on constructing plugins.

0.21.0 to 0.22.0
----------------
* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* This upgrade changes the way the file processors handle uploaded files.  File processing now takes place as a scheduled task rather than immediately after files are uploaded.  For deployments with multiple IRIDA servers running against the same database, prossing may not be performed by the IRIDA server the files were uploaded to and will instead be balanced among all the available servers.  If you want to disable file processing on an IRIDA server, set the following property in `/etc/irida/irida.conf` : `file.processing.process=false`.
* A new pipeline, [bio_hansel](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/bio_hansel/), has been included. You will have to make sure to install the necessary Galaxy tools listed in the documentation.
* The [MentaLiST](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/mentalist/) pipeline has been upgraded. Please make sure to install the necessary tools in Galaxy.
* The [SISTR](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/sistr/) pipeline has been upgraded to make use of [shovill](https://github.com/tseemann/shovill) for assembly. Please make sure to install the `shovill` Galaxy tool. Also, please make sure to follow the additional instructions in <https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/sistr/#address-shovill-related-issues>, which involves some modifications of the conda environment for `shovill`. In particular, you must:

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
* Two new pipelines, [refseq_masher](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/refseq_masher) and [MentaLiST](https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/mentalist), are included with this release.  Additional Galaxy tools will need to be installed.  Please see the linked installation details for each pipeline for more information.

0.19.0 to 0.20.0
----------------

* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* This upgrade removes Dandelion framework from IRIDA.  `-Ddandelion.profile.active=prod"` should be removed from Tomcat settings.  Please see <https://phac-nml.github.io/irida-documentation/administrator/web/#servlet-container-configuration>.
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
* The SISTR pipeline has been upgraded to use `sistr_cmd` 1.0.2.  This new version must be installed in Galaxy.  Please see <https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/sistr/>.

0.15.0 to 0.16.0
----------------

* This upgrade makes schema changes to the databases and cannot be parallel deployed.  Servlet container must be stopped before deploying the new `war` file.
* A new pipeline (SISTR) has been added and requires installation of additional tools and dependencies in Galaxy.  Please see <https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/sistr/>.
* The **SPAdes** tool has been updated to <https://toolshed.g2.bx.psu.edu/view/nml/spades/35cb17bd8bf9>, please update this tool in Galaxy. See <https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/assembly-annotation/> for more details on upgrading.

0.14.0 to 0.15.0
----------------
* A new version of SNVPhyl should be installed in Galaxy (version 1.0.1).  You must install the repository `suite_snvphyl_1_0_1` with revision `4841b7148c44` from the [IRIDA Main Toolshed](https://irida.corefacility.ca/galaxy-shed/view/nml/suite_snvphyl_1_0_1/4841b7148c44).  Please see <https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/phylogenomics/#irida-whole-genome-phylogenomics> for more information.
* Enable Dandelion `prod` profile by setting the environment variable `dandelion.profile.active=prod` in the Tomcat settings.  See https://phac-nml.github.io/irida-documentation/administrator/web/#servlet-container-configuration for more information.
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
* A new version of SNVPhyl should be installed in Galaxy (version 1.0).  You must install the repository `suite_snvphyl_1_0_0` with revision `4e41de612a14` from the [IRIDA Main Toolshed](https://irida.corefacility.ca/galaxy-shed/view/nml/suite_snvphyl_1_0_0/4e41de612a14).  Please see <https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/phylogenomics/#irida-whole-genome-phylogenomics> for more information.
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
* (Optional) Add new keys for exporting data to NCBI's SRA. Please see https://phac-nml.github.io/irida-documentation/administrator/web/#core-configuration for a complete list of the keys to add (all use the prefix `ncbi.upload`). You must create an account with NCBI to enable this feature: http://www.ncbi.nlm.nih.gov/books/NBK47529/#_SRA_Quick_Sub_BK_Establishing_a_Center_A_

1.0.0-alpha6 to 1.0.0-alpha7
----------------------------

* A new version of SNVPhyl should be installed in Galaxy (version 0.3).  You must install the repository `suite_snvphyl_0_3_0` with revision `bb2e651149da` from the [IRIDA Main Toolshed](https://irida.corefacility.ca/galaxy-shed/view/nml/suite_snvphyl_0_3_0/bb2e651149da).  Please see https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/phylogenomics/#irida-whole-genome-phylogenomics for more information.
* A new version of Prokka should be installed in Galaxy (version 1.11.0).  You must install the repository `prokka` with revision `f5e44aad6498` from the [Galaxy Main Toolshed](https://toolshed.g2.bx.psu.edu/view/crs4/prokka/f5e44aad6498).  Please see https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/assembly-annotation/ for more information.

1.0.0-alpha5 to 1.0.0-alpha6
----------------------------
* No special upgrade steps required, just deploy the new `war` file.

1.0.0-alpha4 to 1.0.0-alpha5
----------------------------
* A new version of SNVPhyl should be installed in Galaxy, you must install a new tool repository `suite_snvphyl` with revision `99463e5aef1b` from the IRIDA Main Toolshed. Please see https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/phylogenomics/#irida-whole-genome-phylogenomics for more information.
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
