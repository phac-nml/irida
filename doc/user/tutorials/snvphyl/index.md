---
layout: default
title: "SNVPhyl Whole genome SNV Phylogeny"
search_title: "SNVPhyl Whole genome SNV Phylogeny"
description: "A tutorial on how to construct whole genome SNV phylogenies with SNVPhyl."
---

Building a whole genome SNV Phylogeny with SNVPhyl
==================================================
{:.no_toc}

This is a quick tutorial on how to construct a whole genome SNV phylogeny with [SNVPhyl][] through IRIDA.

* TOC
{:toc}

Initial Data
============

{% include tutorials/common/initial-data.md %}

Reference Genome
----------------

SNVPhyl requires a reference genome to be used for mapping sequencing reads and calling variants.  This must be uploaded to the project containing the samples to use.  A number of example reference files are provided under the `references/` folder in the sample data package.  Please upload the file `08-5578.fasta` using the following steps.

{% include tutorials/common/uploading-a-reference.md %}

Adding Samples to the Cart
==========================

{% include tutorials/common/adding-samples.md %}

Selecting a Pipeline
====================

{% include tutorials/common/selecting-pipeline.md %}

For this tutorial, we will select the **Phylogenomics Pipeline**.

Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![snvphyl-pipeline-page.png][]

Selecting **Customize** brings up a page where parameters can be customized.

![snvphyl-parameters.png][]

The default parameters will often be appropriate but we will modify the **Minimum read coverage** to `10` for this tutorial.  When finished please select **Use these Parameters**.

Once a set of parameters has been chosen, the **Ready to Launch?** button may be used to start the pipeline.

![ready-to-launch-button.png][]

Once the button is selected you should see a screen showing that your pipeline has been launched.

![pipeline-launch.png][]

Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![your-analyses-menu.png][]

The will bring you to a page where you can monitor the status of each launched workflow.

![snvphyl-analysis-status.png][]

Clicking the pipeline name **SNVPhyl_20151117** will bring you to a page for that analysis pipeline.

![snvphyl-analysis-status-details.png][]

This page will continue to refresh as the pipeline progresses through each stage.  It will take a while the SNVPhyl analysis pipeline to complete.

Viewing the Results
===================

Once the pipeline is complete, you will see the generated phylogenetic tree within your browser and you will be given the option to download the results of the analysis.  Please click **Download** to download these results now.

![snvphyl-results.png][]

A number of files are provided within the download package.  These are described below:

1. `vcf2core.tsv`:  This defines the number of core positions evaluated for constructing the phylogeny.
2. `phylogeneticTreeStats.txt`:  This contains additional information about the constructed tree.
3. `phylogeneticTree.newick`:  This contains the constructed phylogenetic tree in newick format.
4. `mappingQuality.txt`:  This defines the percent of the reference covered by each genome.
5. `snvAlignment.phy`:  This defines a multiple sequence alignment of SNVs used to generate the phylogeny.
6. `snvMatrix.tsv`:  This contains a pair-wise SNV distance matrix.
7. `snvTable.tsv`:  This is a table of the individual variants detected.
8. `filterStats.txt`:  This defines information about the SNVs removed due to poor quality.

More information about interpreting these files can be found in the [SNVPhyl Output Guide][].

Viewing Provenance Information
==============================

To view the pipeline provenance information, please select the **Provenance** tab.

![snvphyl-provenance.png][]

This will display the individual steps of this pipeline and the parameters used at each step.  For more details on the pipeline please see the [SNVPhyl][] documentation.

[SNVPhyl]: http://snvphyl.readthedocs.io/
[snvphyl-pipeline-page.png]: images/snvphyl-pipeline-page.png
[snvphyl-parameters.png]: images/snvphyl-parameters.png
[ready-to-launch-button.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/ready-to-launch-button.png
[pipeline-launch.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/pipeline-launch.png
[your-analyses-menu.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-menu.png
[your-analyses-page.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-page.png
[snvphyl-analysis-status.png]: images/snvphyl-analysis-status.png
[snvphyl-analysis-status-details.png]: images/snvphyl-analysis-status-details.png
[SNVPhyl Output Guide]: http://snvphyl.readthedocs.io/en/latest/user/output/
[snvphyl-results.png]: images/snvphyl-results.png
[snvphyl-provenance.png]: images/snvphyl-provenance.png

Advanced SNVPHyl Visualizations
===============================

SNVPHyl Analyses can be combined with metadata from the sample the were run to get a more complete picture.  For more information see  [Advanced Visualizations]({{ site.baseurl }}/user/user/analysis-visualizations).
