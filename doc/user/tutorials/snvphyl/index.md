---
layout: default
title: "SNVPhyl Whole genome SNV Phylogeny"
search_title: "SNVPhyl Whole genome SNV Phylogeny"
description: "A tutorial on how to construct whole genome SNV phylogenies with SNVPhyl."
---

Building a whole genome SNV Phylogeny with SNVPhyl
==================================================

This is a quick tutorial on how to construct a whole genome SNV phylogeny with [SNVPhyl][] through IRIDA.

* TOC
{:toc}

Initial Data
============

Sequence Files
--------------

{% include tutorials/common/initial-data.md %}

Reference Genome
----------------

SNVPhyl requires a reference genome to be used for mapping sequencing reads and calling variants.  This must be uploaded to the project containing the samples to use.  A reference file is provided in the <https://irida.corefacility.ca/downloads/data/irida-sample-data.zip> under the `references/` folder.  Please upload the file `08-5578.fasta` using the following steps.

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

We will set the **Minimum read coverage** to `10` and select **Use these Parameters**.

Once a set of parameters has been chosen, the **Ready to Launch?** button may be used to start the pipeline.

![ready-to-launch-button.png][]

Once the button is selected you should see a screen showing that your pipeline has been launched.

![assembly-pipeline-launch.png][]

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



[SNVPhyl]: http://snvphyl.readthedocs.org/
[snvphyl-pipeline-page.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/snvphyl-pipeline-page.png
[snvphyl-parameters.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/snvphyl-parameters.png
[ready-to-launch-button.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/ready-to-launch-button.png
[assembly-pipeline-launch.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/assembly-pipeline-launch.png
[your-analyses-menu.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-menu.png
[your-analyses-page.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-page.png
[snvphyl-analysis-status.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/snvphyl-analysis-status.png
[snvphyl-analysis-status-details.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/snvphyl-analysis-status-details.png
