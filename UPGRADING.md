Upgrading
=========

This document summarizes the environmental changes that need to be made when
upgrading IRIDA that cannot be automated.

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
