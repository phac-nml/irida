---
layout: default
title: "Bio_Hansel: Salmonella Subtyping"
search_title: "Bio_Hansel: Salmonella Subtyping"
description: "A tutorial on how to type data with Bio_Hansel"
---

Subtyping *Salmonella* data with Bio_Hansel
============================================
This is a quick tutorial on how to use IRIDA to analyze data with the bio_hansel pipeline.


Initial Data
============

The data for this tutorial comes from the IRIDA project directory at `/irida/doc/administrator/galaxy/pipelines/test/bio_hansel/reads`. It is assumed the sequence files in have been uploaded into an appropriate sample as described in the [Web Upload Tutorial][]. 

Adding Samples to the Cart
==========================

Before a pipeline can be run a set of samples and sequence read data must be selected and added to the cart. For this tutorial please select the single sample and click the **Add to Cart** button.

![add-to-cart.png][]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button.png][]

Selecting a Pipeline
====================

Once inside the cart, the **Select a Pipeline** button can be used to select a pipeline to run on the selected samples.

![select-a-pipeline.png][]

For this tutorial, we will select the **Bio_Hansel Pipeline**.

Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![bio_hansel_pipeline_screen.png][]

We will use the default parameters. Please use the **Ready to Launch?** button to start the pipeline.

![ready-to-launch-button.png][]

Once the button is selected you should see a screen showing that your pipeline has been launched.


Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![your-analyses-menu.png][]

The will bring you to a page where you can monitor the status of each launched workflow.

Clicking the pipeline name will bring you to a page for that analysis pipeline. This page will continue to refresh as the pipeline progresses through each stage.  

Viewing the Results
===================

Once the pipeline is complete, you will see the Bio_Hansel pipeline reuslts within your browser and you will be given the option to download the results of the analysis.

![bio_hansel_results][]

[Web Upload Tutorial]: ../web-upload/
[add-to-cart.png]: images/add-to-cart.png
[cart-button.png]: images/cart-button.png
[select-a-pipeline.png]: images/pipeline_selection.png
[bio_hansel_pipeline_screen.png]: images/bio_hansel_pipeline_screen.png
[ready-to-launch-button.png]: images/launch.png
[your-analyses-menu.png]: images/analysis.png
[your-analyses-page.png]: images/your-analyses-page.png
[bio_hansel_results]: images/analysis_complete.png
