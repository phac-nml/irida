---
layout: default
search_title: "Managing Sample Metadata"
description: "Documentation for managing sample metadata in IRIDA."
---

Managing Sample Metadata
========================
{:.no_toc}

Each [project](../project) in IRIDA may contain a collection of samples that corresponds to an isolate. Each sample can contain an indefinite number of metadata terms.

* This comment becomes the toc
{:toc}

Viewing and modifying metadata on a sample
------------------------------------------

Metadata can be [viewed and modified]({{site.baseurl}}/user/user/samples/#viewing-individual-sample-details) on the individual sample.

Bulk import of metadata for samples within a project
----------------------------------------------------

Administrators and project managers can directly upload Excel spreadsheets of metadata directly to a project.  It is expected that one of the columns in the spreadsheet will map to the sample **name** within IRIDA, this can be selected at upload time.

Links to the upload page can be found:
 
1. On the `Project` > `Samples` page, under the `Sample Tools` dropdown menu:
![Bulk upload on project sample page under sample tools menu.](images/project_sample_metadata_import_link.png)

2. On the `Project` > `Linelist` page:
![Bulk upload on project linelist page using link.](images/project_linelist_metadata_import_link.png)

<strong style="background-color: rgba(240, 173, 78, 1.00); padding: 5px; font-weight: bold">Please not that this feature is still under development.</strong>

![Excel Spreadsheet Example](images/spreadsheet.png)

Any excel spreadsheet containing metadata for samples in a project can be uploaded through the IRIDA web interface.  One of the column in the table __must__ correspond to the sample name within the project.  In this example spreadsheet, the `NLEP #` column is the sample name.

THe first step is to select the Excel file containing the data.  Either click on the square label `Click or drop Excel file containing metadata for samples in this project.` or drag and drop the file from your file browser.

![Select spreadsheet](images/upload-selection.png)

After uploading a spreadsheet, the column corresponding to the sample name must be selected.  After selecting the column heading, press the `Preivew metadata to be uploaded` button.

![Select name column.](images/upload-column.png)

Before the metadata upload is completed, metadata that matches sample names and ones that don't are presented.

![Preview Upload](images/upload-preview.png)

Metadata that has a matching sample name are listed in the table `Rows matcthings samples` with the number of matching samples.  Clicking on the `Save valid metadata` button will add the metadata to it's sample and redirect to the linelist page.

![Upload Preview Success](images/upload-preview-success.png)

Metadata that do not have matching sample names are listed in the table `Rows not matching samples` with the number of non-matching samples.  Currently nothing can be done about these.  Try to check the sample names and re-importing the spreadsheet.

![Upload Preview Errors](images/upload-preview-errors.png)

Example Upload
==============

![Demonstration of uploading Excel Spreadsheet](images/upload_bulk_metadata.gif)


