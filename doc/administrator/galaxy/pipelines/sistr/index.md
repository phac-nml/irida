---
layout: default
search_title: "IRIDA SISTR Salmonella Typing"
description: "Install guide for the SISTR pipeline."
---

SISTR Typing
============

This workflow uses the software [sistr_cmd][] for typing of Salmonella genomes which are first assembled using [SPAdes][].  The specific Galaxy tools are listed in the table below.

| Tool Name                 | Owner    | Tool Revision | Toolshed Installable Revision | Toolshed             |
|:-------------------------:|:--------:|:-------------:|:-----------------------------:|:--------------------:|
| **flash**                 | irida    | 4287dd541327  | 0 (2015-05-05)                | [IRIDA Toolshed][]   |
| **filter_spades_repeats** | irida    | f9fc830fa47c  | 0 (2015-05-05)                | [IRIDA Toolshed][]   |
| **assemblystats**         | irida    | 51b76a5d78a5  | 1 (2015-05-07)                | [IRIDA Toolshed][]   |
| **spades**                | nml      | 35cb17bd8bf9  | 4 (2016-08-08)                | [Galaxy Main Shed][] |
| **regex_find_replace**    | jjohnson | 9ea374bb0350  | 0 (2014-03-29)                | [Galaxy Main Shed][] |
| **sistr_cmd**             | nml      | 5c8ff92e38a9  | 3 (2017-06-14)                | [Galaxy Main Shed][] |

To install these tools please proceed through the following steps.

## Step 1: Install Dependencies

Some of these tools require additional dependencies to be installed.  For a cluster environment please make sure these are available on all cluster nodes by installing to a shared directory. This can be done with conda (assuming Galaxy is configured to load up the environment `galaxy` for each tool execution, that is in `env.sh`).

```bash
source activate galaxy
conda install perl-xml-simple perl-time-piece perl-data-dumper
source deactivate
```

## Step 2: Galaxy Conda Setup

Galaxy makes use of [Conda][conda] to automatically install some dependencies for SISTR.  Please verify that the version of Galaxy is >= 16.01 and has been setup to use conda (by modifying the appropriate configuration settings, see [here][galaxy-config] for additional details).  A method to get SISTR to work with a Galaxy version < 16.01 is available in [FAQ/Conda dependencies][].

## Step 3: Install Galaxy Tools

Please install all the Galaxy tools in the table above by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for the appropriate **Tool Name** and installing the appropriate **Toolshed Installable Revision**.

The install progress can be checked by monitoring the Galaxy log file `galaxy/main.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

## Step 4: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [SISTR Typing Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload the sequence reads by going to **Analyze Data** and then clicking on the **upload files from disk** icon ![upload-icon][].  Select the [test/reads][] files.  Make sure to change the **Type** of each file from **Auto-detect** to **fastqsanger**.  When uploaded you should see the following in your history.

    ![upload-history][]

3. Construct a dataset collection of the paired-end reads by clicking the **Operations on multiple datasets** icon ![datasets-icon][].  Please check off the two **.fastq** files and then go to **For all selected... > Build List of dataset pairs**.  You should see a screen that looks as follows.

    ![dataset-pair-screen][]

4. This should have properly paired your data and named the sample **AE014613-699860**.  Enter the name of this paired dataset collection at the bottom and click **Create list**.
5. Run the uploaded workflow by clicking on **Workflow**, clicking on the name of the workflow **SISTR Analyze Reads v0.1 (imported from uploaded file)** and clicking **Run**.  This should auto fill in the dataset collection.  At the very bottom of the screen click **Run workflow**.
6. If everything was installed correctly, you should see each of the tools run successfully (turn green).  On completion this should look like.

    ![workflow-success][]

    If you see any tool turn red, you can click on the view details icon ![view-details-icon][] for more information.

If everything was successfull then all dependencies for this pipeline have been properly installed.

[SPAdes]: http://bioinf.spbau.ru/spades
[galaxy-config]: ../../setup#step-4-modify-configuration-file
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
[IRIDA Toolshed]: https://irida.corefacility.ca/galaxy-shed
[gnuplot]: http://www.gnuplot.info/
[SISTR Typing Galaxy Workflow]: ../test/sistr/sistr.ga
[upload-icon]: ../test/snvphyl/images/upload-icon.jpg
[test/reads]: ../test/sistr/reads
[upload-history]: ../test/sistr/images/upload-history.png
[datasets-icon]: ../test/snvphyl/images/datasets-icon.jpg
[dataset-pair-screen]: ../test/sistr/images/dataset-pair-screen.png
[workflow-success]: ../test/sistr/images/workflow-success.png
[view-details-icon]: ../test/snvphyl/images/view-details-icon.jpg
[conda]: https://conda.io/docs/intro.html
[bioconda]: https://bioconda.github.io/
[sistr_cmd]: https://github.com/peterk87/sistr_cmd
[FAQ/Conda dependencies]: ../../../faq#installing-conda-dependencies-in-galaxy-versions--v1601
