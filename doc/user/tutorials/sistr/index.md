---
layout: default
title: "SISTR: Salmonella In Silico Typing Resource"
search_title: "SISTR: Salmonella In Silico Typing Resource"
description: "A tutorial on how to type data with SISTR."
---

Typing *Salmonella* data with SISTR
===================================
{:.no_toc}

This is a quick tutorial on how to use IRIDA to analyze data with the [*Salmonella in-silico* Typing Resource (SISTR)][sistr-web].

* TOC
{:toc}

Initial Data
============

The data for this tutorial comes from <https://irida.corefacility.ca/downloads/data/irida-sample-data.zip>. It is assumed the sequence files in `miseq-run-salmonella/` have been uploaded into appropriate samples as described in the [Web Upload Tutorial][]. Before starting this tutorial you should have a project with samples that appear as follows:

![tutorial-sistr-samples.png][]

Adding Samples to the Cart
==========================

Before a pipeline can be run a set of samples and sequence read data must be selected and added to the cart. For this tutorial please select the single sample and click the **Add to Cart** button.

![add-to-cart.png][]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button.png][]

Selecting a Pipeline
====================

Once inside the cart all available pipelines will be listed in the main area of the page.

![select-a-pipeline-view.png][]

For this tutorial, we will select the **SISTR Pipeline**.

Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![sistr-pipeline-page.png][]

We will use the default parameters. Please use the **Ready to Launch?** button to start the pipeline.

![ready-to-launch-button.png][]

Once the button is selected you should see a screen showing that your pipeline has been launched.

![pipeline-launch.png][]

Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![your-analyses-menu.png][]

The will bring you to a page where you can monitor the status of each launched workflow.

![your-analyses-page.png][]

Clicking the pipeline name **SISTRTyping_20170418_AE014613** will bring you to a page for that analysis pipeline. This page will continue to refresh as the pipeline progresses through each stage.  It will take a while for the SISTR analysis pipeline to complete.

Viewing the Results
===================

Once the pipeline is complete, you will see the SISTR Typing results within your browser and you will be given the option to download the results of the analysis. For more details on interpreting the results, please see the [IRIDA SISTR Documentation][].

![sistr-results.png][]

Interpreting the Results
========================

For information on interpreting the SISTR results, please refer to the detailed [SISTR Report Documentation][].

Viewing Provenance Information
==============================

To view the pipeline provenance information, please select the **Provenance** tab.

![sistr-provenance.png][]

This will display the individual steps of this pipeline and the parameters used at each step.

[sistr-web]: https://lfz.corefacility.ca/sistr-app/
[Web Upload Tutorial]: ../web-upload/
[tutorial-sistr-samples.png]: images/tutorial-sistr-samples.png
[add-to-cart.png]: images/add-to-cart.png
[cart-button.png]: images/cart-button.png
[select-a-pipeline.png]: images/select-a-pipeline.png
[select-a-pipeline-view.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/select-a-pipeline-view.png
[sistr-pipeline-page.png]: images/sistr-pipeline-page.png
[ready-to-launch-button.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/ready-to-launch-button.png
[pipeline-launch.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/pipeline-launch.png
[your-analyses-menu.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-menu.png
[your-analyses-page.png]: images/your-analyses-page.png
[sistr-results.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/sistr-results.png
[sistr-provenance.png]: images/sistr-provenance.png
[IRIDA SISTR Documentation]: ../../user/sistr/
[SISTR Report Documentation]: ../../user/sistr/#report
