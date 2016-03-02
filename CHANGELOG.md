Changes
=======

1.0.0-alpha9 to 1.0.0-alpha10
-----------------------------
* [Developer]: Upgraded to angularjs 1.5.0.
* [Developer]: When building the VM, don't install a tool more than once (even if multiple workflows depend on the tool).

1.0.0-alpha8 to 1.0.0-alpha9
----------------------------
* [Developer]: Added maven package to install node, npm, and front end assets, then it will build all front end dependencies.
* [Developer]: Replaced Grunt with Gulp for better front-end compilation of assets.
* [UI]: Fixed a bug where clicking on FastQC quality charts attempted to download the images instead of viewing them in the browser.
* [Developer]: Added thread pool for scheduled task submission with new configuration key `irida.scheduled.threads`.
* [UI]: Made navigation tabs on the project samples page instead of buttons below breadcrumbs.
* [Developer]: Chrome is launched only one time for the entire testing suite instead of once per class.
* [UI]: Polling for updates is removed from the analysis details page so that browser sessions don't stay open forever.
* [UI]: Session timeouts increased to 5 minutes from 2 minutes.
* [UI]: Enforce some restrictions on NCBI submissions (at least one set of samples, at least one set of files per sample).
* [UI]: Allow removal of samples from the NCBI submission page.
* [UI]: Administrators may now view all NCBI export submissions from the admin panel.
* [Developer]: Replace grunt with gulp.
* [UI]: Fixed an issue with filtering by file where the sample name file was generated on a Windows machine.
* [Developer]: Add a maven plugin for handling downloading node.js and npm.
* [Developer]: Test suites are executed in parallel instead of in serial, improving test execution times.
* [UI]: Fixed the admin events monitoring page.
* [Developer]: Added some docs and features for doing browser sync with gulp on css and javascript changes.
* [UI]: Reference files may now be uploaded at pipeline submission time in addition to being added to a project.
* [UI]: Fixed a bug where the password reset page was not styled in the same way as the rest of the application.

1.0.0-alpha7 to 1.0.0-alpha8
----------------------------
* [Developer]: Fix an issue where having multiple forward slashes in a URI would result in part of the URI being duplicated in links generated as part of the response.
* [UI]: Fixed a bug where users would have permission to view a project page, but not view a sample page if the sample is attached to multiple projects.
* [Developer]: Upgraded to Dandelion Core, Thymeleaf, and Datatables 1.1.1
* [UI]: Ignore the `__workflow_invocation_uuid__` parameter from Galaxy when importing execution provenance from Galaxy.
* [UI]: Password reset e-mails now include your username.
* [Developer]: Fix up inconsistencies with tables created by Liquibase and tables created by Hibernate.
* [Developer] Removed several unused properties and classes to simplify data model.
* [UI]: Analysis list page reworked to use Datatables.  Now does server side paging for performance increases.
* [UI]: Replace large checkboxes on Project Samples page with standard platform dependant checkboxes.
* [UI]: Allow deleting and cancelling an analysis.
* [UI]: Updated pipeline selection interface by adding the pipeline colour to the background of the name and making the panels equal in height.
* [UI]: Fixed issue where samples in the cart could not be sent to galaxy.
* [UI]: Added ability to upload sequence files to NCBI from the Project/Samples table.
* [UI]: New feature to download complete list of projects either in excel or csv format.
* [UI]: Fixed bug on Analysis Details page and updated style of the progress bar.

1.0.0-alpha6 to 1.0.0-alpha7
----------------------------
* [UI] Feature: When new data is added to a project, both the project and sample modified times are changed so that you can find the most recently uploaded files in a project by sorting on the date modified column of the samples table.
* [UI] Feature: All metadata fields for sample are shown on the samples details page, even if the field does not have an entered value.
* [Workflow]: Upgraded SNVPhyl from version 0.2 to 0.3.
* [Workflow]: Upgraded Prokka in assembly and annotation pipelines from 1.4.0 to 1.11.0 (assembly and annotation to 0.3, assembly and annotation collection to 0.2).
* [UI]: Fixed issue where the organism would overflow on the project page sidebar.
* [UI]: Fixed issue with Analyses Filter overlapping the Analyses table on smaller screens.
* [Developer]: Removed magnific-popups from the project samples page and replace with bootstrap modals through angular-ui.
* [UI]: Fixed issue with downloading a sample with duplicate filenames.  Duplicates are now renamed with Windows style "file (1).fastq" names.
* [UI]: Fixed error with date a user joined a project on the Project Members Page.

1.0.0-alpha5 to 1.0.0-alpha6
----------------------------
* [UI] Feature: Added IRIDA logo to the header, and added a footer with some contact information. Also added a favicon.
* [UI] Feature: Added ability to remove sequencing runs and all uploaded files from the web interface.
* [UI] Feature: Upgrade to the latest version of Phylocanvas for rendering phylogenetic trees from SNVPhyl runs. We were falling back to PhyloSVG in Firefox on Linux because of a bug in Phylocanvas, but the bug has since been fixed, so now everyone gets Phylocanvas!
* [UI] Feature/Bugfix: Updated the "Recent Activities" section to use angular directives. Also fixed a bug with how the links in recent activities were rendered.
* [UI] Feature: Added a feature to filter samples in a project using a file with sample names. 
* [UI] Feature: Paging is now down server side for the projects list, improving performance.
* [Tools] Bugfix: Updated some of the permissions for listing projects for `ROLE_SEQUENCER` so that the new GUI uploader tool works.
* [Developer] Feature: Fixed up a lot of the testing code to evaluate pass or fail based on the content of the page rather than the URL. This fixed timing issues in tests and actually validated some behaviour instead of assuming that stuff worked.
* [Developer] Feature: Add forward and reverse links on paired-end sequencing files to the REST API.
* [Developer] Bugfix: Moved the install directories in the VM to reside on `/home` from `/opt` because packer makes a giant home partition and we were running out of space when Galaxy was running in `/opt`.

1.0.0-alpha4 to 1.0.0-alpha5
----------------------------
* [UI] Feature: Announcements section added to dashboard.
* [UI] Feature: Updated the logo in the top, right-hand corner to use the real IRIDA logo.
* [UI] Feature: Project e-mail subscriptions; you may now subscribe to a project to receive daily digest e-mails showing project events.
* [UI] Papercut: Show better error messages when a problem occurs with sequence file uploads by the web interface. (Issue #318)
* [Workflow]: SNVPhyl is updated to version 0.2 (in sync with NML Galaxy).
* [Tools] Bugfix: Resolved issue where multiple OAuth token requests by the same client using the same user credentials was resulting in duplicated OAuth access tokens being issued. (Issue #324)
* [Tools] Papercut: Remove sequencer ID column from back-end, only use the sample name field from sample sheets to reduce confusion about how data is uploaded to samples.
* [Developer] Removed all unused code for transferring files to arbitrary instances of Galaxy, feature is supported by IRIDA Import tool in NML Galaxy.
* [Developer] Add new top-level data model classes to help distinguish mutable from immutable objects.

1.0.0-alpha3 to 1.0.0-alpha4
----------------------------
* [UI] Bugfix: Fixed adding samples to the cart from associated projects.
* [UI] Bugfix: File downloads should now start instantly instead of after a long delay; Upgraded Dandelion library to work resolve issues with downloading files.
* [Workflow] Bugfix: Pipelines are now executed in the background with the user account that submitted them rather than a system user. This change was made to facilitate running pipelines with federated data sources.
* [UI] Bugfix: Samples from remote associated projects are displayed even if the project has no local associated projects.
* [UI] Bugfix: Fixed showing a "Page not found" page was shown after editing sample details. (Thanks to Matt Walker for finding this one)

* [Developer] removed several unused and unnecessary css files.
* [Developer] conditionally hide certain fields and buttons if an e-mail server is not configured.
* [Developer] Bugfix: fixed a long-standing bug where expressions for showing Thymeleaf properties would sometimes randomly break page rendering by 1) updating the Thymeleaf library to actually show the errors instead of silently ignoring them, and 2) disabling expression caching in Thymeleaf (due to a bug in Spring, bug reported upstream at: https://jira.spring.io/browse/SPR-13247).

1.0.0-alpha2 to 1.0.0-alpha3
----------------------------

* [Workflow] Synchronize the assembly and annotation workflow(s) with NML production Galaxy versions.
* [UI] Updated the projects list UI with an improved table view.
* [UI] Fixed the feature to delete sequence files and sequence file pairs.
* [UI] When viewing a sample, immediately navigate to the files page instead of showing the sample metadata first.
* [UI] Show upload progress when uploading sequence files through a web browser.
* [UI] Update modified date on projects when sequencing data is uploaded.
* [UI] Added recent event for top-up data.
* [UI] Add administrator function to revoke client tokens.
* [UI/Workflow] Add feature to load data from remote instances of IRIDA for local workflow analysis.

* [Developer] Added a feature to help maintain common icon and glyph usage throughout the platform web interface.
* [Developer] Complete sequencing runs can now be deleted from the REST API.
* [Documentation] Various improvements in the install guides for Galaxy, and improved install documentation including download links.
* [Developer] Add build scripts for building complete, self-contained instances of IRIDA in a virtual appliance.
* [Developer] Started migration to using Dandelion for asset management on pages.
* [Developer] Add REST API for interacting with analyses.

* [Developer] Change integration tests to use Liquibase instead of Hibernate hbm2ddl, to more closely resemble production environment.
* [Developer] Change service-layer integration tests to use MySQL database instead of in-memory database