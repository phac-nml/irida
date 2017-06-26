---
layout: default
search_title: "IRIDA Whole Genome Phylogenomics"
description: "Install guide for the SNVPhyl whole genome phylogenomics pipeline."
---

IRIDA Whole Genome Phylogenomics
================================

IRIDA uses the software [SNVPhyl][] for constructing whole genome phylogenies.  The following table lists the dependency tools required by the software.

| Tool Name               | Tool Revision | Toolshed Installable Revision | Toolshed             |
|:-----------------------:|:-------------:|:-----------------------------:|:--------------------:|
| **suite_snvphyl_1_0_1** | 4841b7148c44  | 0 (2017-01-18)                | [IRIDA Main Shed][]  |

To install these tools, please proceed through the following steps.

## Step 1: Install Dependencies

Some of these tools require additional dependencies to be installed.  For a cluster environment please make sure these are available on all cluster nodes by installing to a shared directory. This can be done with conda (assuming Galaxy is configured to load up the environment `galaxy` for each tool execution using the `env.sh` file).

```
source activate galaxy
conda install ncurses perl-readonly
```

## Step 2: Install Galaxy Tools

Please install all the Galaxy tools in the table above by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for the appropriate **Tool Name** and installing the appropriate **Toolshed Installable Revision**.

The install progress can be monitored in the Galaxy log files `galaxy/*.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

## Step 3: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [SNVPhyl Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload a reference genome by going to **Analyze Data** and then clicking on the **upload files from disk** ![upload-icon][] icon in the **Tools** panel.  Select the [pipelines/test/snvphyl/reference.fasta][] file.
3. Upload the sequence reads by going to **Analyze Data** and then clicking on the **upload files from disk**.  Select the [pipelines/test/snvphyl/reads][] files.  Make sure to change the **Type** of each file from **Auto-detect** to **fastqsanger**.  When uploaded you should see the following in your history.

    ![upload-history][]

4. Construct a dataset collection of the paired-end reads by clicking the **Operations on multiple datasets** icon ![datasets-icon][].  Please check off all the **.fastq** files and then go to **For all selected... > Build List of dataset pairs**.  You should see a screen that looks as follows.

    ![dataset-pair-screen][]

5. This should have properly paired your data and named each sample **a**, **b**, and **c**.  Enter the name of this paired dataset collection at the bottom and click **Create list**.
6. Run the uploaded workflow by clicking on **Workflow**, clicking on the name of the workflow **SNVPhyl Pipeline** and clicking **Run**.  This should auto fill in the reference file and dataset collection.  There should be three boxes for filling in runtime parameters.  Please fill in `min_coverage=5`, `min_mean_mapping=30`, and `snv_abundance_ratio=0.75`.  Now, at the very bottom of the screen click **Run workflow**.
7. If everything was installed correctly, you should see each of the tools run successfully (turn green).  On completion this should look like.

    ![workflow-success][]

    If you see any tool turn red, you can click on the view details icon ![view-details-icon][] for more information.

If everything was successfull then all dependencies for this pipeline have been properly installed.

[SNVPhyl]: http://snvphyl.readthedocs.io
[GCC]: https://gcc.gnu.org/
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
[IRIDA Main Shed]: https://irida.corefacility.ca/galaxy-shed
[MUMMer]: http://mummer.sourceforge.net/
[SAMTools]: http://www.htslib.org/
[SAMTools 0.1.18]: http://downloads.sourceforge.net/project/samtools/samtools/0.1.18/samtools-0.1.18.tar.bz2
[BioPerl]: http://www.bioperl.org/wiki/Main_Page
[SNVPhyl Galaxy Workflow]: ../test/snvphyl/snvphyl_workflow.ga
[upload-icon]: ../test/snvphyl/images/upload-icon.jpg
[pipelines/test/snvphyl/reference.fasta]: ../test/snvphyl/reference.fasta
[pipelines/test/snvphyl/reads]: ../test/snvphyl/reads
[upload-history]: ../test/snvphyl/images/upload-history.jpg
[datasets-icon]: ../test/snvphyl/images/datasets-icon.jpg
[dataset-pair-screen]: ../test/snvphyl/images/dataset-pair-screen.jpg
[workflow-success]: ../test/snvphyl/images/workflow-success.png
[view-details-icon]: ../test/snvphyl/images/view-details-icon.jpg
