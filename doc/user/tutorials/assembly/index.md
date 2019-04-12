---
layout: default
title: "Genome Assembly with IRIDA"
search_title: "Genome Assembly with IRIDA"
description: "A tutorial on how to perform de novo assemblies with IRIDA."
---

Performing *de novo* assemblies with IRIDA
==========================================
{:.no_toc}

This is a quick tutorial on how to assemble a set of genomes through IRIDA.

* TOC
{:toc}

Pipeline Overview
=================

The assembly and annotation pipeline built into IRIDA proceeds through the following steps.

1. Paired-end reads are merged using [FLASh][].
2. The merged paired-end reads as well as the unmerged reads are passed to [SPAdes][] to perform a *de novo* assembly.
3. The contigs returned by SPAdes are filtered to remove small and low coverage contigs.
4. The filtered contigs are passed to [Prokka][] for genome annotation.
5. A set of summary statistics are generated for the assembled genome.

Initial Data
============

The data for this tutorial comes from <https://irida.corefacility.ca/downloads/data/irida-sample-data.zip>.  It is assumed the sequence files in `miseq-run-assembly-small/` have been uploaded into appropriate samples as described in the [Web Upload Tutorial][].  Before starting this tutorial you should have a project with samples that appear as:

![assembly-tutorial-samples.png][]

Adding Samples to the Cart
==========================

Before a pipeline can be run a set of samples and sequence read data must be selected and added to the cart.  For this tutorial please select all three samples and click the **Add to Cart** button.

![select-samples.png][]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button.png][]

Selecting a Pipeline
====================

Once inside the cart, all available pipelines will be displayed.

![select-a-pipeline-view.png][]

There are two different types of assembly pipelines available:

1. **Assembly and Annotation Pipeline**:  This is used for assembling and annotating a single genome.
2. **Assembly and Annotation Collection Pipeline**:  This is used for assembling and annotating a collection of genomes and compiling the results into a single downloadable package.

For this tutorial, we will select the **Assembly and Annotation Collection Pipeline**.

Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![assembly-pipeline-page.png][]

We will use the default parameters.  Please select the **Ready to Launch?** button to continue.

![ready-to-launch-button.png][]

Once the button is selected you should see a screen showing that your pipeline has been launched.

![pipeline-launch.png][]

Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![your-analyses-menu.png][]

The will bring you to a page where you can monitor the status of each launched workflow.

![your-analyses-page.png][]

Clicking the pipeline name **AssemblyAnnotationCollection_...** will bring you to a page for that analysis pipeline.

![assembly-pipeline-page-details.png][]

This page will continue to refresh as the pipeline progresses through each stage.  It will take a few minutes for the assembly and annotation pipeline to complete.

Viewing the Results
===================

Once the pipeline is complete, you will be given the option to download the results of the analysis.  Please click **Download** to download these results now.

![assembly-pipeline-results.png][]

A number of files are provided in the downloaded results.  These are described below.

1. `contigs-with-repeats-combined.fasta.zip`: The assembled contigs, after repeats were identified and low coverage/small contigs removed.
2. `assembly-stats-with-repeats-combined.tsv`: A table of assembly statistics for the assembled contigs.
3. `genome-combined.gbk.zip`:  The annotated contigs, in GenBank format.
4. `prokka_stats-combined.txt.zip`:  The stats output from Prokka.
5. `prokka-combined.log.zip`:  The log files from Prokka.
6. `prokka-combined.err.zip`:  The error files from Prokka.
7. `filter-spades-combined.txt.zip`:  Information on the contigs removed and filtering parameters.
8. `contigs-without-repeats-combined.fasta.zip`:  The assembled and filtered contigs, minus any repeat regions.
9. `contigs-all-combined.fasta.zip`:  All contigs output from SPAdes, without any flitering.
10. `spades-combined.log.zip`:  The log files from SPAdes.
11. `flash-combined.log.zip`:  The log files from FLASh used to merge paired-end reads.

Viewing Provenance Information
==============================

To view the pipeline provenance information, please select the **Provenance** tab.

![assembly-provenance.png][]

This will display the individual steps of this pipeline and the parameters used at each step.

[FLASH]: http://ccb.jhu.edu/software/FLASH/
[SPAdes]: http://bioinf.spbau.ru/spades
[Prokka]: http://www.vicbioinformatics.com/software.prokka.shtml
[Web Upload Tutorial]: {{ site.baseurl }}/user/tutorials/web-upload
[assembly-tutorial-samples.png]: images/assembly-tutorial-samples.png
[select-samples.png]: images/select-samples.png
[cart-button.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/cart-button.png
[assembly-pipeline-page.png]: images/assembly-pipeline-page.png
[ready-to-launch-button.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/ready-to-launch-button.png
[select-a-pipeline.png]: images/select-a-pipeline.png
[select-a-pipeline-view.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/select-a-pipeline-view.png
[pipeline-launch.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/pipeline-launch.png
[your-analyses-menu.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-menu.png
[your-analyses-page.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-page.png
[assembly-pipeline-page-details.png]: images/assembly-pipeline-page-details.png
[assembly-pipeline-results.png]: images/assembly-pipeline-results.png
[assembly-provenance.png]: images/assembly-provenance.png
