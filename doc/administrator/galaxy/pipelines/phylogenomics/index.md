---
layout: default
search_title: "IRIDA Whole Genome Phylogenomics"
description: "Install guide for the SNVPhyl whole genome phylogenomics pipeline."
---

IRIDA Whole Genome Phylogenomics
================================

IRIDA uses the software [SNVPhyl][] for constructing whole genome phylogenies.  The following table lists the dependency tools required by the software.

| Tool Name               | Tool Revision   | Toolshed Installable Revision | Toolshed              |
|:-----------------------:|:---------------:|:-----------------------------:|:---------------------:|
| **suite_snvphyl_1_2_3** | [bc72925159fc]  | 0                             | [Galaxy Main Shed][]  |

To install these tools, please proceed through the following steps.

## Step 1: Install Galaxy Tools

Please install all the Galaxy tools in the table above by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for the appropriate **Tool Name** and installing the appropriate **Toolshed Installable Revision**.

The install progress can be monitored in the Galaxy log files `galaxy/*.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

### Possible issues

If, after installing all the above tools and testing the pipeline in IRIDA you get the error `GalaxyResponseException{status=400, responseBody={"err_msg": {"sec_filter|select_genotype"...` as described in <https://github.com/phac-nml/irida/issues/1018> then you may need to take the following steps.

1. Find the existing version of `bcftools_view` installed in Galaxy.
2. Uninstall the existing version of `bcftools_view`.
3. Install the new version of `bcftools_view` as described above.
4. Test out the SNVPhyl pipeline again.

## Step 2: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [SNVPhyl Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload a reference genome by going to **Analyze Data** and then clicking on the **upload files from disk** ![upload-icon][] icon in the **Tools** panel.  Select the [pipelines/test/snvphyl/reference.fasta][] file.
3. Upload the sequence reads by going to **Analyze Data** and then clicking on **Upload File**.  Select **Paste/Fetch data** and paste the following URLs to download these files. Make sure to change the **Type** from **Auto-detect** to **fastqsanger**.

    ```
    https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/test/snvphyl/reads/a_1.fastq
    https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/test/snvphyl/reads/a_2.fastq
    https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/test/snvphyl/reads/b_1.fastq
    https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/test/snvphyl/reads/b_2.fastq
    https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/test/snvphyl/reads/c_1.fastq
    https://phac-nml.github.io/irida-documentation/administrator/galaxy/pipelines/test/snvphyl/reads/c_2.fastq
    ```

   Once the files are uploaded, you may wish to rename each of them in Galaxy so that your history looksl ike the following.

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
[bc72925159fc]: https://toolshed.g2.bx.psu.edu/view/nml/suite_snvphyl_1_2_3/bc72925159fc
[98d5499ead46]: https://toolshed.g2.bx.psu.edu/view/iuc/bcftools_view/98d5499ead46
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
[SNVPhyl Galaxy Workflow]: ../test/snvphyl/snvphyl_workflow.ga
[upload-icon]: ../test/snvphyl/images/upload-icon.jpg
[pipelines/test/snvphyl/reference.fasta]: ../test/snvphyl/reference.fasta
[pipelines/test/snvphyl/reads]: ../test/snvphyl/reads
[upload-history]: ../test/snvphyl/images/upload-history.jpg
[datasets-icon]: ../test/snvphyl/images/datasets-icon.jpg
[dataset-pair-screen]: ../test/snvphyl/images/dataset-pair-screen.jpg
[workflow-success]: ../test/snvphyl/images/workflow-success.png
[view-details-icon]: ../test/snvphyl/images/view-details-icon.jpg
