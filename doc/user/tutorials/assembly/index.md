---
layout: default
title: "Genome Assembly with IRIDA"
search_title: "Genome Assembly with IRIDA"
description: "A tutorial on how to perform de novo assemblies with IRIDA."
---

Performing *de novo* assemblies with IRIDA
==========================================

This is a quick tutorial on how to assemble a set of genomes through IRIDA.

* TOC
{:toc}

Initial Data
============

The data for this tutorial comes from <https://irida.corefacility.ca/downloads/data/irida-sample-data.zip>.  It is assumed the sequence files in `miseq-run/` have been uploaded into appropriate samples as described in the [Web Upload Tutorial][].  Before starting this tutorial you should have a project with samples that appear as:

![tutorial-pipeline-samples.png][]

Adding Samples to the Cart
==========================

The first step to assemble a set of genomes is to select the samples with sequence read data to assemble and add to the cart.  For this tutorial please select all three samples and click the **Add to Cart** button.

![select-samples.png][]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button.png][]

Selecting a Pipeline
====================

Once inside the cart, the **Select a Pipeline** button can be used to select a pipeline to run on the selected samples.

![select-a-pipeline.png][]

From the **Select a Pipeline** view a number of different pipelines are available.

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

![assembly-pipeline-launch.png][]

Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![your-analyses-menu.png][]

The will bring you to a page where you can monitor the status of each launched workflow.

![your-analyses-page.png][]

Clicking the pipeline name **AssemblyAnnotationCollection_...** will bring you to a page for that analysis pipeline.

![assembly-pipeline-page-details.png][]

This page will continue to refresh as the pipeline progresses through each stage.  It will take a while (a few hours) for the assembly and annotation pipeline to complete.

Viewing the Results
===================

Once the pipeline is complete, you will be given the option to download the results of the analysis.  Please click **Download** to download these results now.

[Web Upload Tutorial]: ../web-upload
[tutorial-pipeline-samples.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/tutorial-pipeline-samples.png
[select-samples.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/select-samples.png
[cart-button.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/cart-button.png
[select-a-pipeline.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/select-a-pipeline.png
[select-a-pipeline-view.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/select-a-pipeline-view.png
[assembly-pipeline-page.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/assembly-pipeline-page.png
[ready-to-launch-button.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/ready-to-launch-button.png
[assembly-pipeline-launch.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/assembly-pipeline-launch.png
[your-analyses-menu.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-menu.png
[your-analyses-page.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-page.png
[assembly-pipeline-page-details.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/assembly-pipeline-page-details.png
