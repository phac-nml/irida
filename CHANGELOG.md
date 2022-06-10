
# Changelog

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
* [Developer/Workflow]: Fixed issue with deleting AnalysisSubmission raising null pointer execptions due to bean wiring issue (issue #1287).

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