# Changelog

# [24.12] - 2024/12/20
* [Documentation]: Developer installation documentation updated with new instructions for Java 17. See [PR 1563](https://github.com/phac-nml/irida/pull/1563)
* [UI]: Fixed bug in AnalysisTextPreview component which prevented the loaded file size from being displayed in file preview. See [PR 1560](https://github.com/phac-nml/irida/pull/1560)
* [Developer]: Updated to use Java 17. See [PR 1538](https://github.com/phac-nml/irida/pull/1538)
* [Developer/UI]: Updated to React 18, Ant Design 4.24.16, and switched over to use mdxeditor markdown editor. See [PR 1537](https://github.com/phac-nml/irida/pull/1537)
* [Developer]: Updated node (20.14.0), pnpm (9.12.3), and eslint (9.14.0). See [PR 1524](https://github.com/phac-nml/irida/pull/1524)


# [23.10.1] - 2023/12/07
* [UI]: Fixed issue where filter inputs required focus when they are opened on the project samples page. See [PR 1503](https://github.com/phac-nml/irida/pull/1503)
* [UI]: Fixed bug preventing sorting and paging on the project members page. See [PR 1504](https://github.com/phac-nml/irida/pull/1504)
* [Developer]: Fixed race condition where FastQC file processor would start on incomplete file by filtering out files on non complete sequencing runs. [PR 1506](https://github.com/phac-nml/irida/pull/1506)

## [23.10] - 2023/10/15
* [Developer]: Added functionality to delete sequence files from file system when a sequence run is removed. [See PR 1468](https://github.com/phac-nml/irida/pull/1468)
* [Developer]: Added script to do initial cleanup of sequence files from file system. [See PR 1469](https://github.com/phac-nml/irida/pull/1469)
* [UI]: Add identifier to project drop-down on project synchronization page. See [PR 1474](https://github.com/phac-nml/irida/pull/1474)
* [Developer]: Added functionality to delete sequence files from file storage when removed from sample. [See PR 1476](https://github.com/phac-nml/irida/pull/1476)
* [Developer]: Added override flag that determines if files should be deleted from file storage. [See PR 1486](https://github.com/phac-nml/irida/pull/1486)
* [Developer]: Fixed flaky text in `PipelinesPhylogenomicsPageIT#testPageSetup` test. See [PR 1490](https://github.com/phac-nml/irida/pull/1492)
* [ALL]: Added LDAP/ADLDAP support.
* [Developer]: Added start and ends dates to filesystem clean up script. [See PR 1487](https://github.com/phac-nml/irida/pull/1487)

## [23.01.3] - 2023/05/09
* [Developer]: Fixed issue with metadata uploader removing existing data. See [PR 1489](https://github.com/phac-nml/irida/pull/1489)

## [23.01.2] - 2023/04/17
* [UI]: Fixed bug that caused all metadata fields to be removed when single field was removed from a template. See [PR 1482](https://github.com/phac-nml/irida/pull/1482)
* [Developer]: Fixed bug which allowed duplicated entries in the user_group_project table which prevented the user group from being removed. Fixed bug which was preventing analyses with `html` file outputs from completing. See [PR 1483](https://github.com/phac-nml/irida/pull/1483)
* [Developer]: Fixed flaky PipelinesPhylogenomicsPageIT test. See [PR 1482](https://github.com/phac-nml/irida/pull/1482)
* [REST]: Fixed issues with syncing between newer IRIDA (>22.09) and older IRIDA (<=20.09) by disabling requests for `application/xml` for some of the REST requests and only requesting data as `application/json`. See [PR 1484](https://github.com/phac-nml/irida/pull/1484).

## [23.01.1] - 2023/03/21
* [UI]: Fixed issue where template order was not applied when applying a linelist template. See [PR 1479](https://github.com/phac-nml/irida/pull/1479)
* [UI]: Added select all to associated projects filter on project > samples page. See [PR 1479](https://github.com/phac-nml/irida/pull/1479)

## [23.01] - 2023/02/28
* [UI/Developer]: Updated `react-router` to the version 6.4.3. See[PR 1405](https://github.com/phac-nml/irida/pull/1405)
* [Developer] Updated developer setup documentation, ignore java_pid\*.hprof files, and added quality of life file `gradle.properties`. See [PR 1415](https://github.com/phac-nml/irida/pull/1415).
* [Documentation]: Updated broken links in developer documentation. See [PR 1418](https://github.com/phac-nml/irida/pull/1418).
* [Developer] Updated developer setup documentation, ignore java_pid*.hprof files, and added quality of life file `gradle.properties`
* [UI]: Refreshed global search page to use Ant Design. See [PR 1409](https://github.com/phac-nml/irida/pull/1409)
* [UI/Developer]: Removed old notification system hack and updated to use only Ant Design notifications. See [PR 1447](https://github.com/phac-nml/irida/pull/1447)
* [UI]: Fixed bug where the `User` column was named `User Group` on the admin User Groups page. [See PR 1450](https://github.com/phac-nml/irida/pull/1450)
* [Developer]: Replaced Apache OLTU with Nimbusds for performing OAuth2 authentication flow during syncing and Galaxy exporting. See [PR 1432](https://github.com/phac-nml/irida/pull/1432)
* [Developer/UI]: Performance enhancements to the metadata uploader. See [PR 1445](https://github.com/phac-nml/irida/pull/1445).
* [Developer/UI]: Fix for updating sample modified date when metadata is deleted. See [PR 1457](https://github.com/phac-nml/irida/pull/1457).
* [Developer]: Added support for cloud based storage. Currently, Microsoft Azure Blob and Amazon AWS S3 are supported. [See PR 1194](https://github.com/phac-nml/irida/pull/1194)
* [Developer]: Updated metadata uploader to use react-router-dom Outlet. [See PR 1464](https://github.com/phac-nml/irida/pull/1464)
* [Developer]: Deprecated "/api/projects/{projectId}/samples/bySequencerId/{seqeuncerId}" in favour of "/api/projects/{projectId}/samples/bySampleName", which accepts a json property "sampleName"
* [Developer]: Fixed bug in setting a `default_sequencing_object and default_genome_assembly to `NULL` for a sample when the default sequencing object or genome assembly were removed. [See PR 1466](https://github.com/phac-nml/irida/pull/1466)
* [Developer]: Fixed bug preventing a `sample` with an analysis submission from being deleted. [See PR 1467](https://github.com/phac-nml/irida/pull/1467)

## [22.09.7] - 2023/01/24
* [UI]: Fixed bugs on NCBI Export page preventing the NCBI `submission.xml` file from being properly written. See [PR 1451](https://github.com/phac-nml/irida/pull/1451)

## [22.09.6] - 2022/12/21
* [UI]: Fixed bug on NCBI Export page preventing the export from occuring. See [PR 1439](https://github.com/phac-nml/irida/pull/1439)

## [22.09.5] - 2022/12/14
* [Developer/UI]: Fixed bug preventing a manager of a user group from adding new members when this manager is a collaborator on one of these users projects. Also, fixed issue with a user group member added with an owner role for a group was set with a member role. See [PR 1431](https://github.com/phac-nml/irida/pull/1431)
* [Developer/UI]: Updated synchronize new remote project page to display http errors when setting the url manually and an error is encountered. See [PR 1433](https://github.com/phac-nml/irida/pull/1433)
* [Developer]: Added liquibase migration for renamed NCBI Instrument Models.  See [PR 1436](https://github.com/phac-nml/irida/pull/1436)
* [Developer]: Removed ABI SOLID platforms from NCBI Instrument Models.  See [PR 1436](https://github.com/phac-nml/irida/pull/1436)

## [22.09.4] - 2022/11/14
* [REST]: Fixed issue with project/samples api response missing samples when a sample has a default sequencing object. See [PR 1413](https://github.com/phac-nml/irida/pull/1413)

## [22.09.3] - 2022/11/10
* [REST]: Fixed remote project syncing issues, caused by invalid refresh tokens, invalid status updates, and bad error handling of unauthorized tokens. Also fixed issue with project owners not being to able see remote settings menu or delete menu. See [PR 1410](https://github.com/phac-nml/irida/pull/1410)

## [22.09.2] - 2022/11/04
* [UI]: Fixed bug causing associated project samples to be added to the cart with the wrong project identifier. See [PR 1395](https://github.com/phac-nml/irida/pull/1395)
* [UI]: Fixed bug preventing the removal of locked samples within a project. See [PR 1396](https://github.com/phac-nml/irida/pull/1396)
* [Developer/UI]: Fixed bug preventing managers from sharing project samples. See [PR 1398](https://github.com/phac-nml/irida/pull/1398)
* [UI]: Fixed bug where a sample added to the cart from the sample detail viewer still had a `Add to Cart` button if the viewer was closed and relaunched. See [PR 1397](https://github.com/phac-nml/irida/pull/1397)
* [Galaxy]: Fixed missing "deferred" state found in the Galaxy API but not in the IRIDA API for getting status of Galaxy histories. See [PR 1402](https://github.com/phac-nml/irida/pull/1402).
* [UI]: Fixed a bug that allowed the sharing and moving of locked samples. See [PR 1403](https://github.com/phac-nml/irida/pull/1403)

## [22.09.1] - 2022/10/21
* [UI]: Fixed when sharing or exporting sample on the project sample page, and other minor bugs. See [PR 1382](https://github.com/phac-nml/irida/pull/1382)

## [22.09] - 2022/10/07
* [Developer/UI]: Refreshed the create new user account page. See [PR 1285](https://github.com/phac-nml/irida/pull/1285)
* [Developer/UI]: Added in typescript support to webpack build, moving forward all new frontend development will use typescript. See [PR 1294](https://github.com/phac-nml/irida/pull/1294) for more.
* [Developer/UI]: Removed `styled-components` from page header and replaced with CSS variables. See [PR 1284](https://github.com/phac-nml/irida/pull/1284)
* [Developer/UI]: Updated eslint rule to check for object and array destructuring. See [PR 1322](https://github.com/phac-nml/irida/pull/1322)
* [UI]: Fixed user routes for admin panel. See [PR 1323](https://github.com/phac-nml/irida/pull/1323)
* [Developer/UI]: Added `eslint-prettier-pluggin`. See [PR 1328](https://github.com/phac-nml/irida/pull/1328)
* [Developer]: Replaced Maven with Gradle for build. See [PR 1300](https://github.com/phac-nml/irida/pull/1300)
* [Developer/UI]: Switched to pnpm from yarn for frontend dependency management. See [PR 1332](https://github.com/phac-nml/irida/pull/1334)
* [Developer]: Switched custom exception handling to use built in Spring Boot exception handling. See [PR 1319](https://github.com/phac-nml/irida/pull/1319)
* [UI]: Fixed issue with sorting OAuth clients table in admin panel. See [PR 1342](https://github.com/phac-nml/irida/pull/1342)
* [UI]: Updated share samples review page to list the actual samples which will not be shared with the target project either due to the same sample identifiers or the same samples names already in the target project. See [PR 1343](https://github.com/phac-nml/irida/pull/1343)
* [REST]: Updated synchronizing of sample data to remove sequencing objects and assemblies that no longer exist on the remote sample. See [PR 1345](https://github.com/phac-nml/irida/pull/1345)
* [UI]: Fixed issue with filtering samples by files using a windows encoded text file causing sample name truncation. See [PR 1346](https://github.com/phac-nml/irida/pull/1346)
* [Developer]: Fixed deleting a project with project subscriptions. See [PR 1348](https://github.com/phac-nml/irida/pull/1348)
* [Developer]: Updated OAuth2 implementation to use Spring Security 5 OAuth2 libraries. See [PR 1339](https://github.com/phac-nml/irida/pull/1339)
* [Developer]: Add identifier to project drop-downs. See [PR 1352](https://github.com/phac-nml/irida/pull/1352)
* [UI]: Fixed issue with Biohansel pipeline being launched without selecting an option for a required parameter. See [PR 1356](https://github.com/phac-nml/irida/pull/1356)
* [Developer]: Unified validate sample names into one endpoint. See [PR 1353](https://github.com/phac-nml/irida/pull/1353)
* [Developer/UI]: Increased speed of Project Samples table export and added estimated coverage to Project Samples table and exports. See [PR 1360](https://github.com/phac-nml/irida/pull/1360)
* [Workflow]: Start the Galaxy Data Library timeout when an upload begins rather than when it is first queued up. See [PR 1337](https://github.com/phac-nml/irida/pull/1337)
* [Developer]: Added description and metadata to create & update project sample endpoints. See [PR 1359](https://github.com/phac-nml/irida/pull/1359)
* [UI]: Fix issue where year is displayed incorrectly when the last day of the week for the date is in another year. See [PR 1364](https://github.com/phac-nml/irida/pull/1364)
* [Developer]: Fix issue where large downloads silent failed due to async request timeout. See [PR 1368](https://github.com/phac-nml/irida/pull/1368)
* [Developer]: Update to Spring Boot 2.7.3 and update various other dependencies. See [PR 1369](https://github.com/phac-nml/irida/pull/1369)
* [Developer]: Fixed issue with disabled user accounts requesting a password reset. See [PR 1373](https://github.com/phac-nml/irida/pull/1373)
* [Developer/UI]: Updated sample details view to use Ant Design and moved into a modal which can be launched from anywhere the sample name is listed. See [PR 1370](https://github.com/phac-nml/irida/pull/1370)
* [UI]: Updated analysis results manage results page to not allow a project collaborator to view the save results back to a sample section. See [PR 1377](https://github.com/phac-nml/irida/pull/1377)
* [UI]: Updated phylogenetic tree visualization on the analysis details using Ant Design and the new phylocanvas.gl. See [PR 1280](https://github.com/phac-nml/irida/pull/1280)
* [Developer]: Added security scanning of GitHub pull-requests with [CodeQL](https://codeql.github.com/) (scans for code vulnerabilities) and [Grype](https://github.com/anchore/grype) (scans for Java package dependency vulnerabilities). See [PR 1282](https://github.com/phac-nml/irida/pull/1282).
* [UI]: Updated layout of the analysis view to allow for scrollable sections. See[PR 1378](https://github.com/phac-nml/irida/pull/1378)

## [22.05.5] - 2022/06/28
* [UI]: Fixed bug preventing export of project samples table due to invalid url. [PR 1331](https://github.com/phac-nml/irida/pull/1331)
* [UI]: Updated user account security page to allow admins to change passwords for other users. See [PR 1330](https://github.com/phac-nml/irida/pull/1330)
* [UI]: Updated the NCBI SRA Submission details page to Ant Design.  See [PR 1311](https://github.com/phac-nml/irida/pull/1311)

## [22.05.4] - 2022/06/16
* [UI]: Fixed bug preventing filter samples by file to fail on invalid url. See [PR 1318](https://github.com/phac-nml/irida/pull/1318)

## [22.05.3] - 2022/06/14
* [Developer]: Fix bug where users with only group access to a project couldn't view sample details or metadata. See [PR 1313](https://github.com/phac-nml/irida/pull/1313)
* [Developer]: Fix sql order by bug on admin statistics page that was causing page not to load when `ONLY_FULL_GROUP_BY` enabled. See [PR 1313](https://github.com/phac-nml/irida/pull/1313)

## [22.05.2] - 2022/06/10
* [Developer/UI]: Fix flaky UI Integration tests. See [PR 1313](https://github.com/phac-nml/irida/pull/1313)
* [Developer]: Update war build to decrease size and remove executable ability. See [PR 1313](https://github.com/phac-nml/irida/pull/1313)
* [Developer/UI]: Fixed bug where context path was not set for login and password reset components. See [PR 1313](https://github.com/phac-nml/irida/pull/1313)
* [Developer]: Fixed bug where duplicate project_subscriptions were added for users who had direct project membership with email subscriptions and were also a member of the project through group membership. See [PR 1313](https://github.com/phac-nml/irida/pull/1313)

## [22.05.1] - 2022/06/01
* [UI]: Fixed bug where samples could not be removed from share page. See [PR 1296](https://github.com/phac-nml/irida/pull/1296) for more.
* [Developer]: Fixed bug where project_subscriptions were added for groups without projects. See [PR 1296](https://github.com/phac-nml/irida/pull/1296) for more.

## [22.05] - 2022/05/30
* [Developer]: Add `--no-yarn` to skip the yarn build in `run.sh`
* [Developer]: Updated frontend dependencies: `babel`, `eslint`, `postcss`, and `webpack`
* [Database]: Updated group and user project queries to improve performance for all pages/calls that depend on project permissions.
* [UI]: Updated Create Remote Api to use Ant Design.
* [UI]: Removed old bootstrap client details/edit page and updated with a modal on the clients listing page.
* [UI]: Updated Dashboard Recent Activity to use Ant Design.
* [Developer]: Updated to Ant Design v4.19.3.
* [Developer]: Removed unused DataTables code.
* [UI]: Sorted columns for advanced charts and added labels to tiny charts on admin statistics page.
* [UI]: Fixed: Allow organism name not included in the current taxonomy.
* [Developer]: Removed `prop-types` as a front-end development dependency.
* [UI]: Updated `User` and `Admin` help documentation URLs.
* [Developer/UI]: Refreshed the user account page.
* [UI]: Updated message displayed to user when an invalid reference file is uploaded while launching a workflow.
* [Developer]: Updated nodejs to `v16.15.0` in the `pom.xml` file.
* [Developer]: Updated redux API within the cart page to use Redux Toolkit.
* [All]: Added functionality for project managers to restrict metadata fields depending on the Collaborator's metadata role on the project.
* [Developer]: Removed IRIDA virtual appliance build scripts and references in documentation.
* [Workflow]: Disable the built-in SISTR and the MentaLiST pipelines by default as they are deprecated. They can be re-enabled in `/etc/irida/irida.conf`.
* [Workflow]: Automated pipelines which become marked as disabled will now be prevented from running, similar to out-of-date automated pipelines.
* [Developer]: Upgrade to Apache POI 5.2.2, and switched to excel-streaming-reader 3.6.1 from xlsx-streamer 2.1.0
* [UI]: Added clarification that deleting or removing genomics data will not remove the underlying data files (e.g., sequencing data) but will only make it inaccessible.
* [UI]: Updated project samples page to use Ant Design.
* [Documentation]: Updated SNVPhyl install instructions since installing `bcftools_view` separately is no longer needed. Fixed broken link for sequence read test data. Removed **Search** option since it no longer works in our documentation.
* [UI]: Fixed: Excel output file preview displaying numeric values as a whole number rather than in scientific notation.
* [UI]: Updated the sequence run page to use Ant Design.
* [UI]: Updated `forgot password`, `account activation`, and `reset password` pages to use Ant Design.
* [Developer/Workflow]: Fixed issue with deleting AnalysisSubmission raising null pointer executions due to bean wiring issue (issue #1287).

## [22.03.1] - 2022/04/05
* [ALL]: Upgraded to Spring Boot 2.6.6 which fixes CVE-2022-22965.
* [Developer/UI]: Fixed Project details to be transactional to resolve scalability issue on large projects >5000 samples.
* [Developer/UI]: Fixed updates in UI service classes to use @Transactional annotation to fix scalability issue on large projects >5000 samples.

## [22.03] - 2022/03/23
* [All]: Fixed scalability bug in updating Project modifiedDate during handling of ProjectEvent that was increasing in
  operation time as number of Samples increased within a project.
* [UI]: Fixed load times on Project Samples page.
* [UI]: Fixed text overflow issue in Command-line Linker modal.
* [UI]: Fixed bug with Command-line Linker modal making multiple requests to the server to get the command string.
* [UI]: Fixed issue with CSV/XLSX export on linelist page from not completing on massive projects >100,000 samples.
* [UI]: Adding in batch loading to linelist page (provides feedback to user, important on large projects >5000 samples)
* [UI]: Added a progress indicator when loading samples into the linelist table.
* [UI]: Added a warning notification when selecting all samples on the project samples page.

## [22.01.4] - 2022/02/28
* [UI]: Fixed bug preventing updating of project details. (22.01.4)

## [22.01.3] - 2022/02/24
* [REST]: Fixed bug that was preventing syncing remote projects from versions 20.09 and older.

## [22.01.2] - 2022/02/24
* [Documentation]: Fixed issue that prevented a user from saving analysis results back to a sample and concatenating sequence files. Fixed date format in Swagger REST API documentation.

## [22.01.1] - 2022/02/04
* [REST]: Corrected behaviour of date fields in REST API to return epoch instead of textual string.

## [22.01] - 2022/01/28
* [REST]: Fixed bug prevending REST API clients from updating the `collectedDate` on samples.
* [Developer]: Updated `antd` to version 4.16.13
* [Developer/UI]: Refreshed the metadata uploader.
* [Developer/UI]: Updated to the latest release of `react-router-dom` v6.0.2.
* [UI]: Refactored share/move samples between projects to use a separate page.
* [UI]: Added capability to share/move samples between projects from the line list page.
* [UI]: Fixed issue with dynamic pipeline parameters with only one value not rendering.
* [Developer]: Updated to use Yarn v3 and updated browserlist to v4.19.17.
* [Developer]: Added parsing of CHROMEWEBDRIVER in ./run-tests.sh to detect and use chromedriver provided by github actions ubuntu20.04 image.
* [UI/Workflow]: Added in support for displaying html files found inside of zipped html output files.
* [Developer]: Converted the project from bare Spring to Spring Boot 2.6.3, which involved upgrading a number of
  dependencies.
* [Developer]: Update to `react-router` v6 from `@reach-router`.

## [...previous](https://github.com/phac-nml/irida/blob/21.09.2/CHANGELOG.md)

[Unreleased]: https://github.com/phac-nml/irida/compare/23.01.3...HEAD

[23.01.3]: https://github.com/phac-nml/irida/compare/23.01.2...23.01.3
[23.01.2]: https://github.com/phac-nml/irida/compare/23.01.1...23.01.2
[23.01.1]: https://github.com/phac-nml/irida/compare/23.01...23.01.1
[23.01]: https://github.com/phac-nml/irida/compare/22.09.7...23.01
[22.09.7]: https://github.com/phac-nml/irida/compare/22.09.6...22.09.7
[22.09.6]: https://github.com/phac-nml/irida/compare/22.09.5...22.09.6
[22.09.5]: https://github.com/phac-nml/irida/compare/22.09.4...22.09.5
[22.09.4]: https://github.com/phac-nml/irida/compare/22.09.3...22.09.4
[22.09.3]: https://github.com/phac-nml/irida/compare/22.09.2...22.09.3
[22.09.2]: https://github.com/phac-nml/irida/compare/22.09.1...22.09.2
[22.09.1]: https://github.com/phac-nml/irida/compare/22.09...22.09.1
[22.09]: https://github.com/phac-nml/irida/compare/22.05.5...22.09
[22.05.5]: https://github.com/phac-nml/irida/compare/22.05.4...22.05.5
[22.05.4]: https://github.com/phac-nml/irida/compare/22.05.3...22.05.4
[22.05.3]: https://github.com/phac-nml/irida/compare/22.05.2...22.05.3
[22.05.2]: https://github.com/phac-nml/irida/compare/22.05.1...22.05.2
[22.05.1]: https://github.com/phac-nml/irida/compare/22.05...22.05.1
[22.05]: https://github.com/phac-nml/irida/compare/22.03.1...22.05
[22.03.1]: https://github.com/phac-nml/irida/compare/22.03...22.03.1
[22.03]: https://github.com/phac-nml/irida/compare/22.01.4...22.03
[22.01.4]: https://github.com/phac-nml/irida/compare/22.01.3...22.01.4
[22.01.3]: https://github.com/phac-nml/irida/compare/22.01.2...22.01.3
[22.01.2]: https://github.com/phac-nml/irida/compare/22.01.1...22.01.2
[22.01.1]: https://github.com/phac-nml/irida/compare/22.01...22.01.1
[22.01]: https://github.com/phac-nml/irida/compare/21.09.2...22.01
