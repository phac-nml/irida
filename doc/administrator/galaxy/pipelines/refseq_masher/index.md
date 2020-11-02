---
layout: default
search_title: "refseq_masher"
description: "Install guide for the refseq_masher pipeline."
---

refseq_masher
=============

This workflow uses [refseq_masher] and [Mash] to find what NCBI RefSeq Genomes match or are contained in some input sequence data.  The specific Galaxy tools are listed in the table below.

| Tool Name                 | Owner    | Tool Revision | Toolshed Installable Revision | Toolshed             |
|:-------------------------:|:--------:|:-------------:|:-----------------------------:|:--------------------:|
| `refseq_masher`           | nml      | 26df66c32861  | 0 (2018-02-15)                | [Galaxy Main Shed][] |

To install these tools please proceed through the following steps.

## Step 1: Galaxy Conda Setup

Galaxy makes use of [Conda] to automatically install some dependencies for [refseq_masher].  Please verify that the version of Galaxy is >= v16.01 and has been setup to use conda (by modifying the appropriate configuration settings, see [here][galaxy-config] for additional details).  A method to get [refseq_masher] to work with a Galaxy version < v16.01 is available in [FAQ/Conda dependencies].

## Step 2: Install Galaxy Tools

Please install all the Galaxy tools in the table above by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for the appropriate **Tool Name** and installing the appropriate **Toolshed Installable Revision**.

The install progress can be checked by monitoring the Galaxy log files `galaxy/*.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

## Step 3: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [refseq_masher Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload the sequence reads by going to **Analyze Data** and then clicking on the **upload files from disk** icon ![upload-icon][].  Select the [test/reads][] files.  Make sure to change the **Type** of each file from **Auto-detect** to **fastqsanger**.  When uploaded you should see the following in your history.

    ![upload-history][]

3. Construct a dataset collection of the paired-end reads by clicking the **Operations on multiple datasets** icon ![datasets-icon][].  Please check off the two **.fastq** files and then go to **For all selected... > Build List of dataset pairs**.  You should see a screen that looks as follows.

    ![dataset-pair-screen][]

4. This should have properly paired your data and named the sample **AE014613-699860**.  Enter the name of this paired dataset collection at the bottom and click **Create list**.
5. Run the uploaded workflow by clicking on **Workflow**, clicking on the name of the workflow **RefSeqMasher-sequence_reads_paired-v0.1.0 (imported from uploaded file)** and clicking **Run**.  This should auto fill in the dataset collection.  Click **Run workflow**.

    ![1-workflow-run]

6. If everything was installed correctly, you should see each of the tools run successfully (turn green).  On completion this should look like.

    ![2-workflow-output]
    
    refseq_masher matches output:
    
    ![3-workflow-matches-output]
    
    refseq_masher contains output:
    
    ![4-workflow-contains-output]

    If you see any tool turn red, you can click on the view details icon ![view-details-icon] for more information.

If everything was successfull then all dependencies for this pipeline have been properly installed.

[refseq_masher]: https://github.com/phac-nml/refseq_masher
[Mash]: https://mash.readthedocs.io/en/latest/index.html
[refseq_masher Galaxy Workflow]: ../test/refseq_masher/refseq_masher.ga
[galaxy-config]: ../../setup#step-4-modify-configuration-file
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
[upload-icon]: ../test/snvphyl/images/upload-icon.jpg
[test/reads]: ../test/sistr/reads
[upload-history]: ../test/sistr/images/upload-history.png
[datasets-icon]: ../test/snvphyl/images/datasets-icon.jpg
[dataset-pair-screen]: ../test/sistr/images/dataset-pair-screen.png
[1-workflow-run]: ../test/refseq_masher/images/1-workflow-run.png
[2-workflow-output]: ../test/refseq_masher/images/2-workflow-output.png
[3-workflow-matches-output]: ../test/refseq_masher/images/3-workflow-matches-output.png
[4-workflow-contains-output]: ../test/refseq_masher/images/4-workflow-contains-output.png
[view-details-icon]: ../test/snvphyl/images/view-details-icon.jpg
[Conda]: https://conda.io/docs/intro.html
[FAQ/Conda dependencies]: ../../../faq#4-installing-conda-dependencies-in-galaxy-versions--v1601
