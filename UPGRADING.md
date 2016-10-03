Upgrading
=========

This document summarizes the environmental changes that need to be made when
upgrading IRIDA that cannot be automated.

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
