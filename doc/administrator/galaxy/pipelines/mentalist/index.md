---
layout: default
search_title: "IRIDA MentaLiST MLST Analsyis"
description: "Install guide for the MentaLiST pipeline."
---

MentaLiST MLST Analysis
=======================

This workflow uses the software [MentaLiST][] for typing of microbial samples directly from sequence reads.  The specific Galaxy tools are listed in the table below.

| Tool Name                      | Owner    | Tool Revision | Toolshed Installable Revision | Toolshed             |
|:------------------------------:|:--------:|:-------------:|:-----------------------------:|:--------------------:|
| **mentalist**                  | dfornika | a6cd59f35832  | 9 (2018-06-26)                | [Galaxy Main Shed][] |
| **combine_tabular_collection** | nml      | b815081988b5  | 0 (2017-02-06)                | [Galaxy Main Shed][] |

## Step 1: Galaxy Conda Setup

Galaxy makes use of [Conda][conda] to automatically install some dependencies for MentaLiST.  Please verify that the version of Galaxy is >= v16.01 and has been setup to use conda (by modifying the appropriate configuration settings, see [here][galaxy-config] for additional details).

## Step 2: Install Galaxy Tools

Please install all the `mentalist` Galaxy tool by logging into Galaxy, navigating to **Admin > Search Tool Shed**, searching for `mentalist` and installing the appropriate **Toolshed Installable Revision**.

The install progress can be checked by monitoring the Galaxy log files `galaxy/*.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

## Step 3: Installing MentaLiST kmer Databases

MentaLiST requires an organism-specific kmer database to search against. Log in to Galaxy and navigate to **Admin > Local data**. Under **Run Data Manager Tools**, select **MentaLiST Download from pubMLST**

![mentalist-data-managers][]

Choose your kmer size. The default kmer size of 31 should work well for most applications. From the drop-down menu, select 'Salmonella enterica'.

![mentalist-download-pubmlst][]

A green result box will appear in the galaxy history once the database is complete:

![mentalist-download-pubmlst-result][]

To confirm that the database is installed, select **mentalist_databases** from the list of available Tool Data Tables:

![mentalist-data-table-list][]

Your new MentaLiST database will be listed in the table of available MentaLiST databases. If it doesn't appear, click the refresh button at the top of the table.

![mentalist-database-available][]

## Step 4: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [MentaLiST Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload the sequence reads by going to **Analyze Data** and then clicking on the **upload files from disk** icon ![upload-icon][].  Select the [test/reads][] files.  Make sure to change the **Type** of each file from **Auto-detect** to **fastqsanger**.  When uploaded you should see the following in your history.

    ![upload-history][]

3. Construct a dataset collection of the paired-end reads by clicking the **Operations on multiple datasets** icon ![datasets-icon][].  Please check off the two **.fastq** files and then go to **For all selected... > Build List of dataset pairs**.  You should see a screen that looks as follows.

    ![dataset-pair-screen][]

4. This should have properly paired your data and named the sample **AE014613-699860**.  Enter the name of this paired dataset collection at the bottom and click **Create list**.
5. Run the uploaded workflow by clicking on **Workflow**, clicking on the name of the workflow **MentaLiST MLST v0.1 (imported from uploaded file)** and clicking **Run**.  This should auto fill in the dataset collection.  Ensure that an appropriate kmer database (Salmonella enterica) has been selected before running the workflow. At the very top of the screen click **Run workflow**.
6. If everything was installed correctly, you should see each of the tools run successfully (turn green).  On completion this should look like.

    ![workflow-success][]

    If you see any tool turn red, you can click on the view details icon ![view-details-icon][] for more information.

If everything was successfull then all dependencies for this pipeline have been properly installed.

## Note: Duplicate Data Managers

If multiple versions of the `mentalist` tool have been installed, there will be multiple entries for each Data Manager tool. The **Admin > Local Data** page may look like this:

![mentalist-duplicate-data-managers]

If that is the case, you can remove the duplicates by deactivating mentalist `0.1.3` or any other older versions that may be installed:

![mentalist-deactivate-old-version]

[MentaLiST]: https://github.com/WGS-TB/MentaLiST
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
[galaxy-config]: ../../setup#step-4-modify-configuration-file
[mentalist-data-managers]: ../test/mentalist/images/mentalist-data-managers.png
[mentalist-download-pubmlst]: ../test/mentalist/images/mentalist-download-pubmlst.png
[mentalist-download-pubmlst-result]: ../test/mentalist/images/mentalist-download-pubmlst-result.png
[mentalist-data-table-list]: ../test/mentalist/images/mentalist-data-table-list.png
[mentalist-database-available]: ../test/mentalist/images/mentalist-database-available.png
[mentalist-duplicate-data-managers]: ../test/mentalist/images/mentalist-duplicate-data-managers.png
[mentalist-deactivate-old-version]: ../test/mentalist/images/mentalist-deactivate-old-version.png
[MentaLiST Galaxy Workflow]: ../test/mentalist/mentalist.ga
[test/reads]: ../test/sistr/reads
[upload-icon]: ../test/mentalist/images/upload-icon.png
[upload-history]: ../test/mentalist/images/upload-history.png
[datasets-icon]: ../test/mentalist/images/datasets-icon.png
[dataset-pair-screen]: ../test/mentalist/images/dataset-pair-screen.png
[workflow-success]: ../test/mentalist/images/workflow-success.png
[view-details-icon]: ../test/mentalist/images/view-details-icon.png
[conda]: https://conda.io/docs/
