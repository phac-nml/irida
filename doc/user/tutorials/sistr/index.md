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

![tutorial-sistr-samples]

Adding Samples to the Cart
==========================

Before a pipeline can be run a set of samples and sequence read data must be selected and added to the cart. For this tutorial please select the single sample and click the **Add to Cart** button.

![add-to-cart]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button.png]

Selecting a Pipeline
====================

Once inside the cart all available pipelines will be listed in the main area of the page.

![pipeline-select]

For this tutorial, we will select the **SISTR Pipeline**.

Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![sistr-pipeline-page]

We will use the default parameters. Please use the **Ready to Launch?** button to start the pipeline.

![launch-button]

Once the button is selected you should see a screen showing that your pipeline has been launched.

![pipeline-launched]

Monitoring Pipeline Status
==========================

The will bring you to a page where you can monitor the status of each launched workflow.

![monitor-analyses]

Clicking the pipeline name **SISTRTyping_20200220_AE014613** will bring you to a page for that analysis pipeline. It will take a while for the SISTR analysis pipeline to complete.

Viewing the Results
===================

Once the pipeline is complete, you will see the SISTR Typing results within your browser and you will be given the option to download the results of the analysis. For more details on interpreting the results, please see the [IRIDA SISTR Documentation][].

![sistr-info]

![serovar-predictions]

![cgmlst]

![mash]

You can view the `sistr` analysis output files in tabular, textual and/or json view:

![sistr-output-preview]

Interpreting the Results
========================

For information on interpreting the SISTR results, please refer to the detailed [SISTR Report Documentation][].

Viewing Provenance Information
==============================

To view the pipeline provenance information, please select the **Provenance** tab.

![sistr-provenance]

The provenance is displayed on a per file basis. Clicking on `sistr-predictions.json` file will display it's provenance. Expanding each tool will display the parameters that the tool was executed with.

![sistr-provenance-tools]


Viewing Pipeline Details
========================

To view analysis details, please select the **Settings** tab.

![sistr-settings]

To edit an analysis name, please select the **Pencil** icon next to the analysis name.

![sistr-settings-edit-name]

Add the text `_01` to the end of the name and hit enter.

![sistr-settings-edit-name-updated]

To view samples used by the analysis, please select the **Samples** tab.

![sistr-samples]

To share analysis results with other projects and/or save results back to samples, please select the **Manage Results** tab.

![sistr-manage-results]

To delete an analysis, please select the **Delete Analysis** tab.

![delete-analysis]


[add-to-cart]: images/add-to-cart.png
[cart-button.png]: images/cart-button.png
[cgmlst]: images/cgmlst.png
[delete-analysis]: images/delete-analysis.png
[IRIDA SISTR Documentation]: ../../user/sistr/
[launch-button]: images/launch-button.png
[mash]: images/mash.png
[monitor-analyses]: images/view-analysis-status.png
[pipeline-launched]: images/pipeline-launched.png
[pipeline-select]: images/pipeline-select.png
[select-a-pipeline.png]: images/select-a-pipeline.png
[serovar-predictions]: images/serovar-predictions.png
[sistr-info]: images/sistr-info.png
[sistr-manage-results]: images/sistr-manage-results.png
[sistr-output-preview]: images/sistr-output-preview.png
[sistr-pipeline-page]: images/sistr-pipeline-page.png
[sistr-provenance]: images/sistr-provenance.png
[sistr-provenance-tools]: images/sistr-provenance-tools.png
[SISTR Report Documentation]: ../../user/sistr/#report
[sistr-samples]: images/sistr-samples.png
[sistr-settings]: images/sistr-settings.png
[sistr-settings-edit-name]: images/sistr-settings-edit-name.png
[sistr-settings-edit-name-updated]: images/sistr-settings-edit-name-updated.png
[sistr-web]: https://lfz.corefacility.ca/sistr-app/
[tutorial-sistr-samples]: images/tutorial-sistr-samples.png
[Web Upload Tutorial]: ../web-upload/