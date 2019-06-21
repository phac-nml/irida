Changes
=======

19.05 to 19.09
---------------
* [UI/Developer]: Removed `datatables-bootstrap3-plugin` to remove dependency on outdated `jquery`.
* [UI/Developer]: Removed ImmutableJS from the Line List Page.
* [UI/Developer]: Moved the path of compiled JavaScript and CSS to `src/main/webapp`.
* [REST/Developer]: REST API applications can now select which role to add users to a project.
* [UI/Developer]: Cleaned up some of the redux set up on the linelist page.
* [UI/Developer]: Removed loading Bootstrap through `bower` and moved it into the webpack build.
* [UI/Developer]: Removed loading angularjs through `bower` and moved it into the webpack build.
* [UI/Developer]: Fixed bug where importing an metadata excel worksheet with numbers as column headers would fail.
* [UI/Developer]: Updated `axios` to fix vulnerability.
* [UI/Developer]: Updated `fstream` to fix vulnerability.
* [Developer]: Update pom file to reflect active IRIDA developers.
* [UI]: Fixed bug where a new metadata template could not be created. (19.05.1)
* [UI/Developer]: Updated to latest `react-redux` to use new hooks API.
* [UI]: Updated session expiration modal.
* [UI]: Removed `momentjs` from being loaded on every page.
* [UI]: Removed `livestampjs` as a project dependency.
* [REST]: Changed the URL suffix to analysis output files to be a numerical id instead of a hash key.  Files with a `.` in the name were having issues resolving.  No change in `rel`s so applications should work as usual.
* [UI]: Removed `noty` as a `bower` dependency.
* [Developer]: Removed unnecessary `exists` call in `updateFields` method which was causing some hibernate caching issues.
* [UI]: Removed `noty` as `yarn` dependency, only using `ant.design` notifications.
* [UI/Developer]: Removed `datatables` as a bower dependency.
* [UI/Developer]: Removed unused dependencies from `bower` (`angular-bootstrap-switch`, `animate.css`, `angular-datatables`, `angular-daterangepicker-enhanced`, `lodash`, `clipboard`, and `jszip`)
* [UI]: Removed lighthouse modals from the sequence files page.  Loading full images.  Removed dependency `angular-bootstrap-lightbox` from `bower`.

19.01 to 19.05
---------------
* [Documentation]: Added the CalVer updates to the documentation getting started guide.
* [Documentation]: Added note with link to NGS Linker installation documentation to Command-line Linker modal
* [UI/Developer]: Updated to lodash v4.17.11 to fix security issue. (19.01.1)
* [UI/Developer]: Updated yarn to (1.13.0) and node to (11.10.0).
* [UI]: Added ability for user to select if they would like to receive an email upon pipeline completion.
* [UI/Developer]: Added expose loader to load external dependencies through the vendor bundle.
* [UI/Developer]: Updated jquery to v3.3.1 to fix security issue.
* [UI/Developer]: Updated bootstrap dependencies and cleaned up dependencies by running `yarn install --flat` and resolving dependencies.
* [UI]: Fixed typo when loading data in **Advanced Phylogenomic Visualization** page.
* [Admin]: Added message to add `irida.db.profile` param for Tomcat in docs and upgrading guide.
* [UI/Developer]: Added code splitting to webpack bundles.
* [UI/Developer]: Added checkbox to Create a New Project page for user to lock sample modification when a project is created from samples in the cart.
* [UI/Developer]: Minor JavaScript code cleanup.
* [Developer]: Updated spring security to 4.0.4.RELEASE.
* [Admin]: Made analysis task pool size configurable with `irida.workflow.analysis.threads`. (19.01.2)
* [REST]: Fixes issue where the Sample collection date was synchronized incorrectly, leading to the synced date up to one day off from the original date. (19.01.2)
* [UI]: Fixed bug where uploading a metadata file with a `.` in the header row would cause an error. (19.01.2)
* [UI]: Updated icons for datatables sorting and metadata importer.
* [UI]: Fixed bug where users could not update their email subscriptions to projects. (19.01.2)
* [UI/Developer]: Fixed issue where search for member to add to group resulted in no results found when search term contained capital letters.
* [Documentation]: Added information on fixing `ONLY_FULL_GROUP_BY` sql error to the administrator faq docs.
* [UI]: User on a remote project with a project role of manager has the ability to assign user groups to the project.
* [UI]: Fixed bug preventing associated projects from being loaded into the project samples table. (19.01.3)
* [Developer]: Moved FastQC results out of database to filesystem for a big reduction in database size and performance.  See <https://irida.corefacility.ca/documentation/administrator/upgrades/#1905> for more information.
* [UI]: Added hard wrap on sample name on sample details page.
* [UI]: New dedicated cart page.
* [UI]: Exporting to Galaxy now runs through the new cart interface.  Export to Galaxy through the Project Samples page has been deprecated.
* [Developer]: Updated Travis CI dist to `xenial`.
* [Developer]: Falling back to mysql for TravisCI testing.  MariaDB currently having install issues.
* [Developer]: Updated chromedriver in pom.xml and packages.json to newer versions to work better with newest chrome version.
* [UI]: Removed `.xlsx` files from being previewed in the pipeline results page.
* [UI/Developer]: Fixed issue with deleting a user group if it is linked to any projects.
* [UI]: Fixes issue where importing an excel file that contained "Created Date" and "Modified Date" created metadata fields with those labels.
* [REST]: Updated http status code returned (400 Bad Request) when uploading files to the wrong type of run
* [UI/Developer]: Updated to jquery v3.4.0 to fix security issue.
* [Database]: Fixed issue where FastQC description was being stored with an invalidly formatted version in the database.
* [UI]: Fixed bug causing issues with saving Line List templates.
* [UI]: Fixed bug when selecting all samples on the project samples page would not add them to cart.

0.22.0 to 19.01
----------------
* [Admin]: Updated versioning to a [CalVer](https://calver.org/) scheme of YY.0M.MICRO.  New feature releases will have the appropriate the year and month fields, where bugfixes will increment the MICRO field.
* [Database]: Fixed an issue where metadata entries derived from pipelines were not updating the associated analysis submission and ignorning blank entries. (0.23.5)
* [Developer]: Added classes `.jar` maven export in build process.
* [UI]: Added the sample coverage to the table exported from the project samples page.
* [UI/Workflow]: Added option to disable workflows/analysis types from display in IRIDA using `irida.workflow.types.disabled`. (0.22.1)
* [Developer]: Added wait when NCBI Uploader fails before retrying. (0.22.1)
* [UI]: Users can now download in batch their user-generated, shared with project and automated project single sample analysis output files by selecting the files they wish to download from tables on the `/analysis/user/analysis-outputs`, `/projects/<id>/analyses/shared-outputs`, and `/projects/<id>/analyses/automated-outputs` pages, respectively.
* [UI]: Added configurable warning for analysis results and metadata pages.  Set the text for this warning with `irida.analysis.warning`.  This can be used to communicate that results of analyses may be preliminary.
* [Admin]: Added new profiles to allow IRIDA web server to run in a clustered fashion.  See documentation at https://irida.corefacility.ca/documentation/administrator/web/#multi-web-server-configuration
* [UI]: Fixed bug where all moved samples were locked. (0.22.2)
* [UI/Developer]: Updated to lodash v4.17.10 to fix security issue. (0.22.2)
* [UI]: Fixed bug where project samples page would freeze if there where numerous QC Issues. (0.22.3)
* [UI]: Fixed error where synchronizing sequence files could lead to truncated files without generating an error. (0.22.3)
* [UI]: Adding NCBI SRA accession to sample metadata when uploading data to NCBI.
* [Developer]: Updated FastQC to 0.11.7.
* [UI]: Fixed bug in sample edit page that didn't allow users to clear a field in the sample.
* [Workflow]: Updated the AssemblyAnnotation pipeline to v0.5 and the AssemblyAnnotationCollection pipeline to v0.4. Both pipelines now use Shovill for assembly and QUAST for assembly quality assessment in addition to Prokka for annotation.
* [API]: Fixed REST endpoint mapping for current user and user projects.
* [Workflow]: Fixed issue where duplicate filenames were found after running Galaxy workflow in newer Galaxy versions. (0.22.4)
* [Developer]: Added unused import checking to checkstyle config.  The `mvn site` build will throw an error if unused imports are present.
* [Developer/Workflow]: Added the ability build pipelines into independent JAR files to be loaded in IRIDA as a plugin (after placing in `/etc/irida/plugins`). Please see <https://github.com/phac-nml/irida-plugin-example> and <https://irida.corefacility.ca/documentation/developer/tools/pipelines/> for more details.
* [Developer]: Added additional FTP settings for NCBI uploads: `ncbi.upload.controlKeepAliveTimeout`, and `ncbi.upload.controlKeepAliveReplyTimeoutMilliseconds`.
* [Developer]: Fixed issue with some settings in `/etc/irida/irida.conf` not being detected properly.
* [Developer]: Added ability to adjust `jdbc.pool.maxWait` through an environment variable `DB_MAX_WAIT_MILLIS` for fixing timeout issues for tests.
* [Developer]: Split Galaxy testing into `galaxy_testing` and `galaxy_pipeline_testing` to reduce the time it takes for all Galaxy tests to complete.
* [Developer]: Fixed up test cases for genome assemblies and simplified saving to database.
* [Sync]: Project sync date will be updated at start of sync job to stop quickly repeating errored syncs.
* [UI]: IRIDA will now remove local samples when a synchronized remote sample is removed at its source.
* [UI]: New project line list page with inline editing.
* [Developer]: Updated Node, Yarn, and front-end webpack packages.
* [Workflow]: Fixed Shovill Galaxy tool revision for SISTR and Assembly/Annotation pipelines.
* [Developer]: Update to ag-grid-community v.19.1.2.
* [Documentation]: Changed references from GitLab to GitHub in docs.
* [UI]: Removed angular-resource, angular-messages, angular-sanitize, angular-animate, angular-datatables, ng-table and angular-drag-and-drop-lists.
* [REST]: Added method to greatly increase speed of listing samples in a project.  This was becoming an issue for projects with metadata and >5k samples.
* [Developer]: Added pull request and issue templates for github.
* [Developer]: Update Docker Galaxy container to Galaxy 18.09.
* [Administration]: Updated method for automatically installing tools in Galaxy to use [Ephemeris](https://ephemeris.readthedocs.io/en/latest/readme.html).
* [Developer]: Updated to version 20.0.0 of ag-grid UI component.
* [UI]: Add link back to sample for analysis input files on the Analsysis Details Page.
* [UI]: Fixes issue where attempting to select all samples with a filter applied selected all samples in project.
* [UI]: Fixed issue with exporting samples to galaxy through project/samples page failing.

0.21.0 to 0.22.0
----------------
* [UI]: Fixed bug where `.xls` file could not be uploaded through the file picker on the metadata upload page. (0.21.1)
* [Workflow]: Added version 0.1.9 of the [MentaLiST](https://github.com/WGS-TB/MentaLiST) pipeline, which includes a fix for downloading cgMLST schemes and a distance matrix output.
* [UI]: Fixed bug where concatenate files was POSTing to incorrect URL. (0.21.2)
* [UI]: Fixed bug where SVG files could not be exported through the advanced visualization page. (0.21.2)
* [UI]: Fixed bug where users could not share more than nine samples. (0.21.2)
* [UI]: Moved the position of the notification system to top center.
* [Workflow]: Added version 2.0.0 of a pipeline for running [bio_hansel](https://github.com/phac-nml/bio_hansel) (version 2.0.0)
* [Workflow]: Added version 0.3 of a pipeline for running [SISTR](https://github.com/peterk87/sistr_cmd/) which now makes use of [Shovill](https://github.com/tseemann/shovill) for genome assembly.
* [Workflow]: Updated SISTR pipeline to store the following additional fields in the metadata table: serogroup, O antigen, H1, H2, and alleles matching genome.
* [UI]: Users can save analysis results to samples after pipeline is done in "Share Results" tab.
* [UI]: Fixed bug where edit groups page would throw a server exception. (0.21.3)
* [UI]: Hiding user page project list for non-admins.
* [Workflow]: Fixed bug where auto updating metadata from analysis submission failed for non-admin user. (0.21.4)
* [UI]: Fixed bug where admin dropdown menu was hidden behind sequencing run sub navigation.
* [Developer]: Moved file processing chain outside of SequencingObjectService.  It now runs as a scheduled task.  This will help balance the processing load in multi-server deployments.
* [UI]: Ensuring `ROLE_SEQUENCER` users get "Access Denied" for any attempted UI interactions.
* [Developer]: Updated `yarn` to the current version.
* [UI/Workflow]: Pipeline analysis output files are rendered in the same order as they appear in the pipeline `irida_workflow.xml` in the `<outputs>` XML element.
* [Developer]: Can now specify which `chromedriver` to use in UI testing with `-Dwebdriver.chrome.driver=/PATH/TO/chromedriver`.
* [UI]: Fixes slow Sample cart. Quicker saving of large selections of samples to cart (`POST /cart/add/samples`) and loading of existing cart Samples (`GET /cart`).  

0.20.0 to 0.21.0
----------------
* [Workflow]: Added version 0.1 of a pipeline for running [MentaLiST](https://github.com/WGS-TB/MentaLiST) (version 0.1.3).
* [Workflow]: Added version 0.1 of a pipeline for running Mash against the refseq database [refseq_masher](https://github.com/phac-nml/refseq_masher).
* [UI]: Fixed bug where user could not cancel the upload of a sequence file on the Sample Files page.
* [UI/Workflow]: Fixed bug where users could not submit large analyses due to an HTTP 414 "Request URI Too Long" error.
* [Developer]: Removed old gulp dependencies from the `package.json` file.
* [Developer]: Update to stable releases of `node` and `yarn`.
* [Administration]: Disabled automated SISTR results from saving to sample metadata.  Also disabled retrospective results from being added during the database update.  Installations that have already performed the 0.20.0 update will have their retrospective automated SISTR results automatically added to sample metadata.  Installations that jump directly to 0.20.1 and above will not have this data added to sample metadata. (0.20.1)
* [UI/Workflow]: Preview analysis output files in a tabular or plain-text view in the analysis details page under the Preview tab. 

0.19.0 to 0.20.0
----------------
* [Developer]: Fixed exception being thrown related to permission denied for updating samples when a normal user (collaborator on a project) runs the assembly pipeline (0.19.1).
* [UI]: Allowing admins to manually prioritize high importance analyses.
* [Developer]: Removed dandelion from project > samples page.
* [UI]: Fixed issue where Projects table could not be exported (0.19.2).
* [UI]: Fixed user menu icons misaligning in Firefox (0.19.2).
* [Developer]: Updated front end templating engine to Thymeleaf v3.
* [Administration]: Added option to expire passwords after a configured number of days.  Set `security.password.expiry` in `/etc/irida/irida.conf` to configure.
* [Administration]: Limiting users from reusing passwords.
* [Developer]: Updated webpack compile path to be `resources/dist`.
* [Developer]: Webpack now extracts `css` / `scss` into its own bundles.
* [UI]: Added minification to production javascript.
* [UI]: Fixed issue where delete project button was always enabled, and created an error when clicked. (0.19.3)
* [UI]: Clean up of the main navigation bar code, and removed its dependency on angular-ui.
* [UI]: Fixed reflow layout of pipeline launch page.
* [UI]: Changed the wording of 'copying' samples to 'sharing' samples.
* [UI]: Allow users to share (copy) samples from a remote project. Disabled menu items for move and merge.
* [Developer]: Ran `prettier` on all javascript files within `resources/js`.
* [Developer]: Ran `prettier` on all scss files within `resources/sass`.
* [Developer]: Add a git pre-commit hook to ensure `prettier` formatting.
* [UI]: Fixed issue where all activities page could not be displayed.
* [UI]: Fixed issue where time stamps where not displayed on activities pages.
* [UI]: Fixed issue where breadcrumbs were not displaying on the Project > Line List and Project > Analyses pages.
* [UI]: Cleaned up sub-navigation elements on samples and files.
* [UI]: Updated Remote API pages (datatables, and removed Noty modals).  
* [UI]: Fixed issue where breadcrumbs not displaying on the Samples > File > QC Analsis pages.
* [UI]: Removed search box from sequencing run page.
* [UI/Workflow]: Galaxy job error info retrieved from Galaxy if a workflow submission fails. Job error info is shown in Analyses table and on the Analysis page if it exists. 
* [UI]: Fixed issue with Upload Sequence Files button when SequenceFiles page resized.
* [UI]: SISTR able to write metadata back to samples.

0.18.0 to 0.19.0
----------------
* [Developer]: Removed the requirement for pipeline developers to add an `Analysis` subclass and database tables for pipelines.  All pipeline results can now be stored in the `Analysis` class.
* [Developer]: Fixed issue where bootstrap was being loaded twice onto the page. (0.18.1)
* [UI]: Fixed URL for concatenation of sample sequence files. (0.18.1)
* [Developer]: Removed dandelion from: Announcements, Cart, Sequencing Runs, Login, Project Settings - Landing, Events, Create Sample, Line List, Announcements - Create & Read, Livestampjs, Pipelines Launch, lodash, NCBI Export Page, Session Handler, Project Members and Groups Server Side,  NCBI export lists, Groups Listing, Group Members.
* [UI]: Added empty state if no files exist in a sample.
* [UI]: Added ability to associate assemblies with a sample.
* [UI]: Fix broken link for concatenating files. (0.18.2)
* [UI]: Fix broken permissions for downloading reference files not associated with a project. (0.18.2)
* [UI]: Fixed issued with layout of the events panel on the dashboard page. (0.18.2)
* [UI]: Fixed issue with local samples causing problems in synchronized projects. (0.18.2)
* [API]: Fixed permission issue when asking for permission for analysis results using sequencing objects with no associated samples (0.18.3).
* [UI]: Cleaned up styles on DataTables with fixed columns.
* [Developer]: Upgraded to v5.3.0 of `npm`, and changed build process to use `yarn`.
* [API]: NCBI uploads automatically retry after failure.
* [UI]: Added global project and sample search from top toolbar.
* [UI]: Changed user's modified date to "Last Login" in user list.
* [Developer]: Created `production` and `development` webpack builds.
* [UI]: Fixed issue with uploading `.fastqc` files in IE, and allow for uploading for `fastqc.gz` in all browsers (0.18.4).
* [UI]: Added ability to update an analysis submission name after it's been submitted.
* [Developer]: Added `run-tests.sh` script for running local integration tests.
* [UI]: Fixed issue with exporting `csv` and `excel` from Project > Samples pages (0.18.5).
* [UI]: Fixed bug with importing large `excel` file that contained columns with only a header and no other data in column.
* [UI]: Fixed project sync settings from resetting themselves.

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
