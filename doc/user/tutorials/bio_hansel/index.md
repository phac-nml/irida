---
layout: default
title: "bio_hansel: SNV Subtyping"
search_title: "bio_hansel: SNV Subtyping"
description: "A tutorial on how to subtype whole-genome sequencing data with bio_hansel"
---

Subtyping *Salmonella* data with bio_hansel
============================================
This is a quick tutorial on how to use IRIDA to analyze data with the bio_hansel pipeline.


Tutorial Data
=============

The data for this tutorial comes from the EMBL-EBI ENA sequencing run sample [SRR1203042] (please download the [forward reads] and [reverse reads]). 

It is assumed the [forward reads] and [reverse reads] in `fastq.gz` format have been uploaded into an appropriate sample as described in the [Web Upload Tutorial]. 


Adding Samples to the Cart
==========================

Before a pipeline can be run a set of samples and sequence read data must be selected and added to the cart. For this tutorial please select the single sample and click the **Add to Cart** button.

![add-to-cart]

Once the desired samples have been added to the cart, click the **Cart** button at the top navigation bar:

![cart-button]


Selecting a Pipeline
====================

Once inside the cart, the **Select a Pipeline** button can be used to select a pipeline to run on the selected samples.

For this tutorial, we will select the **bio_hansel Pipeline**: 
![select-a-pipeline]


Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters. You will be required to **select a SNV subtyping scheme** to use for your analysis.

![bio-hansel-pipeline-page]

Please select the **Salmonella Heidelberg SNV Subtyping Scheme** and ensure that the **Save bio_hansel results to Project Line List Metadata** checkbox is checked:

![bio-hansel-pipeline-params]

You can leave the other parameters unmodified. Please use the **Ready to Launch?** button to start the pipeline.

![launch-button]

Once the button is selected you should see a screen showing that your pipeline has been launched.


Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![monitor-analyses]

The will bring you to a page where you can monitor the status of each launched workflow.

Clicking the pipeline name will bring you to a page for that analysis pipeline. 


Viewing Individual Sample Results
=================================

Once the pipeline is complete, you will be able to view the `bio_hansel` pipeline results and download the output files of the analysis.

![bio-hansel-results]

You can view the `bio_hansel` analysis output files in tabular or json view:

![bio-hansel-output-files]

You can view the detailed `bio_hansel` match results in a tabular view:

![bio-hansel-match-results]


Viewing Results For Multiple Samples
====================================

If you had checked the **Save bio_hansel results to Project Line List Metadata** checkbox on the `bio_hansel` pipeline launch page, you will be able to view the results of your analyses in the **Line List** table on the **Project** page:

![bio-hansel-linelist]


Interpreting the Results
========================

For more information on interpreting your `bio_hansel` results, please see:

- the [IRIDA bio_hansel Documentation][docs] or 
- the [bio_hansel GitHub] page.


Viewing Provenance Information
==============================

To view the pipeline provenance information, please select the **Provenance** tab.

![bio-hansel-provenance]

The provenance is displayed on a per file basis. Clicking on `bio_hansel_tech-results.json` file will display it's provenance. Expanding each tool will display the parameters that the tool was executed with.

![bio-hansel-provenance-tools]


Viewing Pipeline Details
========================

To view analysis details, please select the **Settings** tab.

![bio-hansel-settings]

To edit an analysis name, please select the **Pencil** icon next to the analysis name.

![bio-hansel-settings-edit-name]

Add the text `_01` to the end of the name and hit enter.

![bio-hansel-settings-edit-name-updated]

To view samples used by the analysis, please select the **Samples** tab.

![bio-hansel-samples]

To share analysis results with other projects and/or save results back to samples, please select the **Manage Results** tab.

![bio-hansel-manage-results]

To delete an analysis, please select the **Delete Analysis** tab.

![delete-analysis]




[add-to-cart]: images/add-to-cart.png
[bio_hansel GitHub]: https://github.com/phac-nml/bio_hansel
[bio-hansel-linelist]: images/biohansel-linelist.png
[bio-hansel-match-results]: images/biohansel-output-files-detailed-match-results.png
[bio-hansel-output-files]: images/biohansel-output-files.png
[bio-hansel-pipeline-params]: images/bio_hansel-pipeline_launch-selected_scheme-save_to_sample_metadata.png
[bio-hansel-provenance]: images/bio-hansel-provenance.png
[bio-hansel-provenance-tools]: images/bio-hansel-provenance-tools.png
[bio-hansel-samples]: images/bio-hansel-samples.png
[bio-hansel-settings]: images/bio-hansel-settings.png
[bio-hansel-settings-edit-name]: images/bio-hansel-settings-edit-name.png
[bio-hansel-settings-edit-name-updated]: images/bio-hansel-settings-edit-name-updated.png
[bio-hansel-manage-results]: images/bio-hansel-manage-results.png
[bio-hansel-pipeline-page]: images/bio_hansel-pipeline_launch-initial.png
[bio-hansel-results]: images/biohansel-results.png
[cart-button]: images/cart-button.png
[delete-analysis]: images/delete-analysis.png
[docs]: ../../user/bio_hansel/
[forward reads]: ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR120/002/SRR1203042/SRR1203042_1.fastq.gz
[launch-button]: images/launch.png
[monitor-analyses]: images/view-analysis-status.png
[reverse reads]: ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR120/002/SRR1203042/SRR1203042_2.fastq.gz
[select-a-pipeline]: images/select-a-pipeline.png
[SRR1203042]: https://www.ebi.ac.uk/ena/data/view/SRR1203042&display=html
[Web Upload Tutorial]: ../web-upload/
