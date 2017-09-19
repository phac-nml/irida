Changes
=======

0.18.0 to 0.19.0
----------------
* [Developer] Removed the requirement for pipeline developers to add an `Analysis` subclass and database tables for pipelines.  All pipeline results can now be stored in the `Analysis` class.
* [Developer]: Removed dandelion from: Announcements, Cart, Sequencing Runs, Login, Project Settings - Landing, Events
* [Developer]: Fixed issue where bootstrap was being loaded twice onto the page. (0.18.1)
* [UI]: Fixed URL for concatenation of sample sequence files. (0.18.1)
* [Developer]: Removed dandelion from: Announcements, Cart, Sequencing Runs, Login, Project Settings - Landing, Events, Create Sample, Associated Projects
* [UI]: Added empty state if no files exist in a sample.

0.17.0 to 0.18.0
----------------
* [Developer]: Removed old javascript build configuration files.
* [Developer]: Cleaned up javascript imports (removed all `require` statements).
* [UI]: Removed phylocanvas context menu from analysis > details page.
* [Workflow]: Made workflows applying to a single sample to include sample name in all output files. Updated previous output files in database to also include sample name if corresponding workflow used only a single sample.
* [UI]: Added buttons on the project settings page, and metadata template page to download an excel template.
* [Developer]: Reorganized permissions classes into subpackages.
* [UI]: Added maximum coverage quality control option.
* [UI]: Allowing admins to delete synchronized data so it will be resynchronized if there was a problem. Note any changes made to synchronized samples will be overwritten on the next sync job.  (0.17.1)
* [Developer]: Checking if single end files have been synchronized to stop duplication.
* [Developer]: Changed relationship of sequence files to a single collection in AnalysisSubmission instead of a collection for each file type.
* [Documentation]: Rewrote Galaxy installation guide, added sections for linking IRIDA to existing Galaxy or to a pre-build Docker image.
* [Developer]: Removed `Dandelion` dependencies from the dashboard, projects listing, & users listing pages.
* [Developer]: Created new DataTables request handler to enable removing Dandelion DataTables.
* [API]: Performance improvements when listing analyses of a specific type in the REST API.
* [Developer]: Changed file processing chain to try to avoid errors in processing.
* [API]: Added REST API endpoints for accessing all analyses associated with a project and for accessing automated analyses.
* [UI]: Added ability to concatenate sequence files in a sample.
* [UI]: New feature to export metadata through the Project line list view.
* [UI]: Fixed bug allowing empty library name for Galaxy exporting.
* [UI]: Added ability to download individual analysis output files.

0.16.0 to 0.17.0
----------------
* [Developer]: Updated how project samples ajax controller handles datatables column sort functionality.
* [UI]: Disallow spaces in Remote API and Client Details creation.
* [UI]: Added ability for admins and project owners to delete a project.
* [UI]: Added `Technician` system role which allows technicians to view results of all sequencing runs in the system.
* [Developer]: Fixed permissions for NCBI submission uploads failing with Access Denied error. (0.16.1)
* [Developer]: Removed `thymeleaf-extras-conditionalcomments`. Not used anymore.
* [UI]: Fixed bug causing reference file uploads to fail when launching a pipeline. (0.16.2)
* [UI]: In SISTR report, changed incorrect label **Percent shared k-mers** to **Mash distance** and report the mash distance instead of converting to percent.
* [Developer]: Changed gitlab CI builds to run with Docker.  See `ci/README.md` for more information.
* [Developer]: Removed deprecated `RemoteRelatedProject` and `*Snapshot` classes and all associated services, repositories, and web features.  Note this feature will remove some tables from the databse such as `remote_related_project` and `remote_sequence_file`.  While these were likely unused it is **strongly** recommended to backup your database before this upgrade.
* [UI]: Fixed bug with permission not allowing users to view automated SISTR results. (0.16.3)
* [Developer]: Fixed permissions for NCBI submission uploads failing with Access Denied error. (0.16.1)
* [UI]: New Line List page for displaying sample metadata for all samples within a project.
* [UI]: New upload excel spreadsheet of metadata for samples within a project.
* [UI]: New project sample metadata templates.
* [UI]: New advanced phylogenetic tree with metadata visualization.
* [UI]: Fixed bug where a project collaborator could try to change the role of a group.
* [UI]: Fix bug where notifications were not being displayed when samples where copied between projects.
* [Workflow]: The SISTR pipeline has been upgraded to use `sistr_cmd` version 1.0.2.

0.15.0 to 0.16.0
----------------
* [Developer]: Upgraded to AngularJS 1.6.2 <https://github.com/angular/angular.js/blob/master/CHANGELOG.md>
* [Developer]: Removed FontAwesome thymeleaf dialect, updated developer documentation <https://irida.corefacility.ca/documentation//developer/interface/icons/>.
* [Workflow]: Added version 0.1 of a pipeline for Salmonella typing (SISTR) using the [sistr_cmd](https://github.com/peterk87/sistr_cmd).
* [UI]: Removing commas (`,`) and replacing spaces with underscores (`_`) in download filenames as they confuse browsers. (0.15.1)
* [Workflow]: The software **SPAdes** has been updated in all workflows to version 3.9.0.  This requires installing an updated `spades` tool in Galaxy.
* [Workflow]: The parameters to **Prokka** were changed to avoid crashing when a contig (and so sample) name was too long.
* [UI]: Fixed issue caused when trying to use the command line linker to export a large number of samples from a project.
* [Developer]: Fixed issue caused when trying to create a new pipeline with no parameters.
* [UI]: Moved "Members", "Groups", "Associated Projects", "Reference Files" to a new "Settings" tab in projects.
* [UI]: Fixed bug where collaborators on a project had access to the sample tools. These buttons are now hidden from collaborators.
* [UI]: Fixed bug with email subscriptions where users were emailed events from all their projects if they were subscribed to one.

0.14.0 to 0.15.0
----------------
* [API]: Added a checksum to files on upload so uploaders can validate upload was successful.
* [UI]: Project sync jobs ignore bad files and set a sample to error when it can't transfer files, but gracefully continues the rest of the sync. (0.14.1)
* [Workflow]: Removed `@Transactional` from the AnalysisExecutionServiceGalaxyAsync.executeAnalysis method as transactions were timing out while waiting for files to upload to galaxy. (0.14.2)
* [Developer]: Cleaned up old use deprecated method `CrudService.update(Long, Map)` and removed the method from the project.
* [API]: Added configurable maximum number of workflows IRIDA will schedule at a time `irida.workflow.max-running`.  Default 4.
* [UI]: Fixed login error message that requested an email instead of username.
* [Developer]: Added quick failure on detection of an upload error in Galaxy.
* [Developer]: Added ability to adjust size of thread pool for polling Galaxy after uploading files with `galaxy.library.upload.threads`.
* [Workflow]: Upgraded SNVPhyl from 1.0 to 1.0.1. Details of changes found at <http://snvphyl.readthedocs.io/en/latest/install/versions/#version-101>.
* [Workflow]: Removed `@Transactional` from methods in `AnalysisExecutionServiceGalaxy` as this was causing occasional conflicts between database entries written under different threads, causing analysis pipelines to get stuck in an invalid state.
* [Administration]: Recommending enabling `prod` profile for dandelion for performance improvements.
* [UI]: Fixed bug for users the could not select all samples on the Project > Samples page.
* [UI]: Fixed bug where pipeline customization button could not be clicked in browser width > 758px and < 991px.
* [UI]: Added a QC indicator to the project/samples table to indicate file processing failure or low coverage.  Also visible in the sample/files page.

0.13.0 to 0.14.0
----------------
* [UI]: Added ability to select or deselect viewing all associated projects. (0.13.1)
* [UI]: Fixed issue with missing settings tab when in export view within a Project. (0.13.2)
* [UI]: Fixed bug preventing sample selection while filtered by file. (0.13.2)
* [UI]: Fixed bug with selecting samples with associated projects. (0.13.3)
* [UI]: Fixed bug when displaying samples names from associated projects when filtering by file. (0.13.3)
* [UI]: Fixed a bug when selecting samples after filter by file with a large number of sample names. (0.13.4)
* [UI]: Added filtering project samples by organism.
* [API]: Added ability to upload sequencing runs as regular user as long as you have access to required projects.
* [UI]: Share analyses with projects so anyone on the project can view analysis results.

0.12.0 to 0.13.0
----------------
* [Developer]: Updated node to v6.4.0.
* [UI]: Fixed bug where sample edit button wouldn't show up when users had permission (0.12.1)
* [UI]: Fixed bug where announcements couldn't be marked as read (0.12.1)
* [UI]: Changed the user icon in recent events to a basic icon instead of a gravatar (0.12.1)
* [API]: Fixed a bug with the sequencingObject/sequence file REST endpoint that was throwing 404s when it should read a file. (0.12.2)
* [Developer]: Added ability to build IRIDA package `.zip` file and `tools-list.yml` for distribution and easy installation of tools in Galaxy.
* [UI]: Major update to the Project > Samples page.
* [Administation]: Added configurable values for the number of threads to use for file processing.
* [Developer]: Creating the file processors as Spring beans and wrapping their processing methods in transactions.

0.11.0 to 0.12.0
----------------
* [UI]: Removed bootstrap tooltip and extra script from dashboard page.
* [UI]: Remove bower dependency for angular-notification-icons.
* [UI]: Remove bower dependency for AngularJS-toaster.
* [UI]: Removed qTip2 dependency.
* [UI]: Removed bower dependency for `bootstrap-sass-official`.
* [UI]: Remove bower dependency for 'MagnificPopup'.
* [UI]: Removed client side dependency on `angular-gravatar` and replace with server gravatar url creation.
* [UI]: Fix issue with Dandelion filters throwing `nullpointerexception`s (0.11.4)
* [Developer]: Added `FetchMode.SELECT` to `SequenceFilePair.files` as Hibernate was including the children multiple times in the result set. (0.11.1)
* [Developer]: Packer now waits for tomcat to start during the build so that the database is fully deployed before distribution.
* [Developer]: Packer renames the VM on output.
* [Developer]: The Virtualbox appliance that's built now uses the `virtio` network adapter because the Intel one had serious performance issues over NAT.
* [UI]: FastQC images were not rendering in Firefox because the request behaviour changed. (0.11.2)
* [UI]: Fixed a permissions issue with reading NCBI submissions. (0.11.3)
* [Developer]: Paths in the database (for sequence files, output files, references, remote) are stored as relative paths instead of absolute. This will make it easier to migrate filesystems in the future.
* [UI]: Implemented a project synchronization feature to pull remote samples and data from other IRIDA installations.
* [Developer]: Added FastQC metrics to the REST API
* [Workflow]: Upgraded SNVPhyl from 0.3 to 1.0.

1.0.0 to 0.11.0
---------------
* [UI]: Fixed issue were login page was being displayed even though the user was logged in.
* [UI]: Full analysis name is now displayed in table
* [UI]: Added the project ID to all project specific pages.
* [UI]: Added the time to modified dates.
* [UI]: Fixed issue with IE11 caching ajax calls.
* [UI]: Samples in the cart are now sorted by created date.
* [UI]: Fixed a bug where exporting to Galaxy from the cart was not working.
* [Database] Fixed an issue with migrating single end sequence files to the new SequencingObject model in cases where samples with single files had been merged.  IRIDA installs should skip directly to v1.0.2 from 1.0.0alpha-10 to avoid database update problems. (1.0.2)
* [Developer]: Added a complete docker image for Galaxy, updated the VirtualBox appliance to use the Docker image.
* [UI]: Fixed a bug when the launch pipeline page was opened on a small display (<1000px) the "Launch Pipeline" button disappeared.
* [Developer]: Added support for a wider variety of naming patters when pairing uploaded sequence files.
* [UI]: Fixed a bug where the `hashCode` method on `SequenceFilePair` was *only* using the date the pair was created, causing `Set`s of pairs to be much smaller than expected. (1.0.3)
* [UI]: Fixed a UI bug in Internet Explorer 11 where the IRIDA logo and the Projects menu were overlapping.
* [Developer]: Fixed a couple of broken links in documentation.
* [Developer]: Re-fixed the `hashCode` bug so that the files collection is a `List` instead of a `Set`.  The `Set` was throwing a `NullPointerException` from Hibernate. (1.0.4)
* [UI]: Displaying the modification time of samples in the project/samples table.
* [UI]: Refactored Sequencing Runs list to use dandelion datatables.
* [UI]: Fixed a bug where groups on a project with manager role were not allowed to behave like a manager. (1.0.5)
* [UI]: Added a project event for removing samples from a project.
* [UI]: Updated NCBI SRA uploader UI to be easier to prepare large numbers of samples.
* [Developer]: Dropped version down to a *slightly* lower number.

1.0.0-alpha10 to 1.0.0
----------------------
* [UI]: Fixed a bug where project filtering on the projects table excluded projects that did not have an organism
* [Developer]: Make all of the responses when working with samples have the same set of links.
* [UI]: Show the version of the pipeline on the pipeline details page.
* [Developer]: Fixed a bug where updating project modified time on addition of a sample caused the uploader to fail.
* [UI]: Help links and contact information shown on page are now configurable.
* [UI]: Show the current version of IRIDA in the UI under the 'Help' menu.
* [Developer] Change integration tests to use Liquibase instead of Hibernate hbm2ddl, to more closely resemble production environment.
* [Developer] Change service-layer integration tests to use MySQL database instead of in-memory database
* [UI] Added description box for describing a new analysis before submission to a pipeline.
* [UI] Added password restriction list when creating or editing user info, and when resetting a password.
* [Developer] Changed organization of sequencing data to make it easier to add other types of sequencing objects and automate tasks on data upload.
* [UI/Workflow] Added per-project option to automatically assemble uploaded sequence data.
* [UI]: Added active tokens count to OAuth client list page.

1.0.0-alpha9 to 1.0.0-alpha10
-----------------------------
* [Developer]: Upgraded to angularjs 1.5.0.
* [Developer]: When building the VM, don't install a tool more than once (even if multiple workflows depend on the tool).
* [UI]: Fixed a bug where the Send to Galaxy button was enabled on the samples table even if you didn't come to IRIDA through a Galaxy.
* [UI]: Fixed a bug where uploading a reference file with IUPAC ambiguous bases was reporting a *very* bad error message, and was inconsistently applied in the different places where you can upload reference files.
* [Developer]: Database initialization for integration tests is now 1) managed by liquibased to match our production environment, and 2) runs much more quickly by loading up a pre-populated database rather than building the database by changesets from the beginning of time.
* [UI]: Removed the bottom footer and moved links into the Help menu.
* [UI]: Fixed a bug where sorting users on the users table didn't actually sort the users correctly.
* [Developer]: Added support to Galaxy integration to allow different sets of states, (hopefully) allowing the use of newer versions of Galaxy that we prescribe.
* [UI]: New feature: user group management for creating collections of users to apply permissions to projects.

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
