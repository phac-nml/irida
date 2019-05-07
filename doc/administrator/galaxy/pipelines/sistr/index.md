---
layout: default
search_title: "IRIDA SISTR Salmonella Typing"
description: "Install guide for the SISTR pipeline."
---

SISTR Typing
============

This workflow uses the software [sistr_cmd][] for typing of Salmonella genomes which are first assembled using [shovill], which uses [SPAdes] for assembly only and performs pre-assembly read correction with [Lighter] and post-assembly correction with [BWA MEM] and [PILON].  The specific Galaxy tools are listed in the table below.

| Tool Name                 | Owner    | Tool Revision   | Toolshed Installable Revision | Toolshed             |
|:-------------------------:|:--------:|:---------------:|:-----------------------------:|:--------------------:|
| **shovill**               | iuc      | [f698c7604b3b]  | 2 (2018-10-07)                | [Galaxy Main Shed][] |
| **sistr_cmd**             | nml      | [5c8ff92e38a9]  | 3 (2017-06-14)                | [Galaxy Main Shed][] |

To install these tools please proceed through the following steps.

## Step 1: Galaxy Conda Setup

Galaxy makes use of [Conda][conda] to automatically install some dependencies for SISTR.  Please verify that the version of Galaxy is >= v16.01 and has been setup to use conda (by modifying the appropriate configuration settings, see [here][galaxy-config] for additional details).  A method to get SISTR to work with a Galaxy version < v16.01 is available in [FAQ/Conda dependencies][].

{% include administrator/galaxy/pipelines/shovill-0.9.0.md %}

## Step 2: Install Galaxy Tools

Please install all the Galaxy tools in the table above by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for the appropriate **Tool Name** and installing the appropriate **Toolshed Installable Revision**.

The install progress can be checked by monitoring the Galaxy log files `galaxy/*.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

## Step 3: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [SISTR Typing Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload the sequence reads by going to **Analyze Data** and then clicking on the **upload files from disk** icon ![upload-icon][].  Select the [test/reads][] files.  Make sure to change the **Type** of each file from **Auto-detect** to **fastqsanger**.  When uploaded you should see the following in your history.

    ![upload-history][]

3. Construct a dataset collection of the paired-end reads by clicking the **Operations on multiple datasets** icon ![datasets-icon][].  Please check off the two **.fastq** files and then go to **For all selected... > Build List of dataset pairs**.  You should see a screen that looks as follows.

    ![dataset-pair-screen][]

4. This should have properly paired your data and named the sample **AE014613-699860**.  Enter the name of this paired dataset collection at the bottom and click **Create list**.
5. Run the uploaded workflow by clicking on **Workflow**, clicking on the name of the workflow **SISTR Analyze Reads v0.3 (imported from uploaded file)** and clicking **Run**.  This should auto fill in the dataset collection.  At the very bottom of the screen click **Run workflow**.
6. If everything was installed correctly, you should see each of the tools run successfully (turn green).  On completion this should look like.

    ![workflow-success]

    If you see any tool turn red, you can click on the view details icon ![view-details-icon][] for more information.

If everything was successfull then all dependencies for this pipeline have been properly installed.


[f698c7604b3b]: https://toolshed.g2.bx.psu.edu/view/iuc/shovill/f698c7604b3b
[5c8ff92e38a9]: https://toolshed.g2.bx.psu.edu/view/nml/sistr_cmd/5c8ff92e38a9
[SLURM]: https://slurm.schedmd.com
[PILON]: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4237348/
[BWA MEM]: https://github.com/lh3/bwa
[Lighter]: https://genomebiology.biomedcentral.com/articles/10.1186/s13059-014-0509-9
[SPAdes]: http://bioinf.spbau.ru/spades
[shovill]: https://github.com/tseemann/shovill/
[galaxy-config]: ../../setup#step-4-modify-configuration-file
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
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
[conda environment]: https://conda.io/docs/user-guide/tasks/manage-environments.html#saving-environment-variables
[GALAXY_MEMORY_MB]: https://planemo.readthedocs.io/en/latest/writing_advanced.html#developing-for-clusters-galaxy-slots-galaxy-memory-mb-and-galaxy-memory-mb-per-slot
