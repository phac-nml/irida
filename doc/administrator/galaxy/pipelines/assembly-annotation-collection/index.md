---
layout: default
search_title: "IRIDA Assembly and Annotation Collection"
description: "Install guide for the assembly and annotation collection pipeline."
---

Assembly and Annotation Collection
==================================

This workflow can assemble and annotate multiple genomes in one submission.  The results from one submission will be packaged together into a single file.  The workflow uses the [shovill] and [Prokka][] software for assembly and annotation of genomes, respectively, as well as [QUAST] for assembly quality assessment.  The specific Galaxy tools are listed in the table below.

| Tool Name                  | Owner    | Tool Revision    | Toolshed Installable Revision | Toolshed             |
|:--------------------------:|:--------:|:----------------:|:-----------------------------:|:--------------------:|
| **bundle_collections**     | nml      | [705ebd286b57][] | 2 (2020-08-24)                | [Galaxy Main Shed][] |
| **shovill**                | iuc      | [8d1af5db538d][] | 5 (2020-06-28)                | [Galaxy Main Shed][] |
| **prokka**                 | crs4     | [111884f0d912][] | 17 (2020-06-04)               | [Galaxy Main Shed][] |
| **quast**                  | iuc      | [ebb0dcdb621a][] | 8 (2020-02-03)                | [Galaxy Main Shed][] |


To install these tools please proceed through the following steps.

## Step 1: Galaxy Conda Setup

Galaxy makes use of [Conda][conda] to automatically install some dependencies for this workflow.  Please verify that the version of Galaxy is >= v16.01 and has been setup to use conda (by modifying the appropriate configuration settings, see [here][galaxy-config] for additional details).  A method to get this workflow to work with a Galaxy version < v16.01 is available in [FAQ/Conda dependencies][].

{% include administrator/galaxy/pipelines/shovill-1.0.4.md %}

## Step 2: Install Galaxy Tools

Please install all the Galaxy tools in the table above by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for the appropriate **Tool Name** and installing the appropriate **Toolshed Installable Revision**.

The install progress can be checked by monitoring the Galaxy log files `galaxy/*.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

**Note**: Prokka downloads several large databases and may take some time to install.

## Step 3: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [Assembly Annotation Collection Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload the sequence reads by going to **Analyze Data** and then clicking on the **upload files from disk** icon ![upload-icon][].  Select the [test/reads][] files.  Make sure to change the **Type** of each file from **Auto-detect** to **fastqsanger**.  When uploaded you should see the following in your history.

    ![upload-history][]

3. Construct a dataset collection of the paired-end reads by clicking the **Operations on multiple datasets** icon ![datasets-icon][].  Please check off the two **.fastq** files and then go to **For all selected... > Build List of dataset pairs**.  You should see a screen that looks as follows.

    ![dataset-pair-screen][]

4. This should have properly paired your data and named the sample **a**.  Enter the name of this paired dataset collection at the bottom and click **Create list**.
5. Run the uploaded workflow by clicking on **Workflow**, clicking on the name of the workflow **AssemblyAnnotationCollection-shovill-prokka-paired_reads-v0.5 (imported from uploaded file)** and clicking **Run**.  This should auto fill in the dataset collection.  At the very bottom of the screen click **Run workflow**.
6. If everything was installed correctly, you should see each of the tools run successfully (turn green).  On completion this should look like.

    ![workflow-success][]

    If you see any tool turn red, you can click on the view details icon ![view-details-icon][] for more information.

If everything was successful then all dependencies for this pipeline have been properly installed.

[705ebd286b57]: https://toolshed.g2.bx.psu.edu/view/nml/bundle_collections/705ebd286b57
[8d1af5db538d]: https://toolshed.g2.bx.psu.edu/view/iuc/shovill/8d1af5db538d
[111884f0d912]: https://toolshed.g2.bx.psu.edu/view/crs4/prokka/111884f0d912
[ebb0dcdb621a]: https://toolshed.g2.bx.psu.edu/view/iuc/quast/ebb0dcdb621a
[galaxy-config]: ../../setup#step-4-modify-configuration-file
[SLURM]: https://slurm.schedmd.com
[PILON]: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC4237348/
[SPAdes]: http://bioinf.spbau.ru/spades
[shovill]: https://github.com/tseemann/shovill/
[Prokka]: http://www.vicbioinformatics.com/software.prokka.shtml
[QUAST]: http://quast.sourceforge.net/quast.html
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
[Assembly Annotation Collection Galaxy Workflow]: ../test/assembly-annotation-collection/assembly-annotation-collection.ga
[upload-icon]: ../test/snvphyl/images/upload-icon.jpg
[test/reads]: ../test/assembly-annotation/reads
[upload-history]: ../test/assembly-annotation/images/upload-history.jpg
[datasets-icon]: ../test/snvphyl/images/datasets-icon.jpg
[dataset-pair-screen]: ../test/assembly-annotation/images/dataset-pair-screen.jpg
[workflow-success]: ../test/assembly-annotation/images/workflow-success.png
[view-details-icon]: ../test/snvphyl/images/view-details-icon.jpg
[FAQ]: ../../../faq/#tbl2asn-out-of-date
[conda]: https://conda.io/docs/intro.html
[bioconda]: https://bioconda.github.io/
[FAQ/Conda dependencies]: ../../../faq#4-installing-conda-dependencies-in-galaxy-versions--v1601
[conda environment]: https://conda.io/docs/user-guide/tasks/manage-environments.html#saving-environment-variables
