IRIDA Whole Genome Phylogenomics
================================

IRIDA uses the software [SNVPhyl][] for constructing whole genome phylogenies.  The following table lists the dependency tools required by the software.

| Tool Name            | Tool Revision | Toolshed Installable Revision | Toolshed             |
|:--------------------:|:-------------:|:-----------------------------:|:--------------------:|
| **msa_datatypes**    | 70227007b991  | 0 (2014-04-22)                | [Galaxy Main Shed][] |
| **bcftools_view**    | 6572c40a8505  | 8 (2012-10-08)                | [Galaxy Main Shed][] |
| **samtools_mpileup** | 973fea5b4bdf  | 3 (2014-03-27)                | [Galaxy Main Shed][] |
| **sam_to_bam**       | 8176b2575aa1  | 4 (2014-03-27)                | [Galaxy Main Shed][] |
| **core_pipeline**    | 0737c0310cab  | 0 (2014-10-07)                | [IRIDA Main Shed][]  |
| **freebayes**        | 386bc6e45b68  | 0 (2014-10-07)                | [IRIDA Main Shed][]  |
| **phyml**            | b5867c5c7674  | 0 (2014-10-07)                | [IRIDA Main Shed][]  |
| **smalt_collection** | de3e46eaf5ba  | 0 (2014-10-07)                | [IRIDA Main Shed][]  |

To install these tools, please proceed through the following steps.

## Step 1: Install Dependencies

Some of these tools require additional dependencies to be installed.  For a cluster environment please make sure these are available on all cluster nodes by installing to a shared directory.

1. [MUMMer][]:  Please download and install MUMMer (in particular, the command `nucmer`) and add your `PATH` in the `$GALAXY_ENV` file.
2. [SAMTools][]: Please download and install [SAMTools 0.1.18][] and add to your `PATH` in the `$GALAXY_ENV` file.
3. **Perl Modules**:  Please download and install dependency Perl modules with the command.

    ```bash
    cpanm Clone Parallel::ForkManager
    ```

    In addition, [BioPerl][] version 1.6.901 must be installed.  Please run the following command to install.

    ```bash
    cpanm http://search.cpan.org/CPAN/authors/id/C/CJ/CJFIELDS/BioPerl-1.6.901.tar.gz
    ```

## Step 2: Install Galaxy Tools

Please install all the Galaxy tools in the table above by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for the appropriate **Tool Name** and installing the appropriate **Toolshed Installable Revision**.

The install progress can be monitored in the Galaxy log file `$GALAXY_BASE_DIR/main.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

## Step 3: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [SNVPhyl Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload a reference genome by going to **Analyze Data** and then clicking on the **upload files from disk** ![upload-icon][] icon in the **Tools** panel.  Select the [pipelines/test/snvphyl/reference.fasta][] file.
3. Upload the sequence reads by going to **Analyze Data** and then clicking on the **upload files from disk**.  Select the [pipelines/test/snvphyl/reads][] files.  Make sure to change the **Type** of each file from **Auto-detect** to **fastqsanger**.  When uploaded you should see the following in your history.

    ![upload-history][]

4. Construct a dataset collection of the paired-end reads by clicking the **Operations on multiple datasets** icon ![datasets-icon][].  Please check off all the **.fastq** files and then go to **For all selected... > Build List of dataset pairs**.  You should see a screen that looks as follows.

    ![dataset-pair-screen][]

5. This should have properly paired your data and named each sample **a**, **b**, and **c**.  Enter the name of this paired dataset collection at the bottom and click **Create list**.
6. Run the uploaded workflow by clicking on **Workflow**, clicking on the name of the workflow **SNVPhyl Pipeline** and clicking **Run**.  This should auto fill in the reference file and dataset collection.  And the very bottom of the screen click **Run workflow**.
7. If everything was installed correctly, you should see each of the tools run successfully (turn green).  On completion this should look like.

    ![workflow-success][]

    If you see any tool turn red, you can click on the view details icon ![view-details-icon][] for more information.

If everything was successfull then all dependencies for this pipeline have been properly installed.

[SNVPhyl]: https://irida.corefacility.ca/gitlab/analysis-pipelines/snvphyl-galaxy/tree/development
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
[IRIDA Main Shed]: https://irida.corefacility.ca/galaxy-shed
[MUMMer]: http://mummer.sourceforge.net/
[SAMTools]: http://www.htslib.org/
[SAMTools 0.1.18]: http://downloads.sourceforge.net/project/samtools/samtools/0.1.18/samtools-0.1.18.tar.bz2
[BioPerl]: http://www.bioperl.org/wiki/Main_Page
[SNVPhyl Galaxy Workflow]: test/snvphyl/snvphyl_workflow.ga
[upload-icon]: test/snvphyl/images/upload-icon.jpg
[pipelines/test/snvphyl/reference.fasta]: test/snvphyl/reference.fasta
[pipelines/test/snvphyl/reads]: test/snvphyl/reads
[upload-history]: test/snvphyl/images/upload-history.jpg
[datasets-icon]: test/snvphyl/images/datasets-icon.jpg
[dataset-pair-screen]: test/snvphyl/images/dataset-pair-screen.jpg
[workflow-success]: test/snvphyl/images/workflow-success.jpg
[view-details-icon]: test/snvphyl/images/view-details-icon.jpg
