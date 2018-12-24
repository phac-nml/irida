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

![](images/add-to-cart.png)

Once the desired samples have been added to the cart, click the **Cart** button at the top navigation bar:

![](images/cart-button.png)


Selecting a Pipeline
====================

Once inside the cart, the **Select a Pipeline** button can be used to select a pipeline to run on the selected samples.

For this tutorial, we will select the **bio_hansel Pipeline**: 
![](images/select-a-pipeline.png)


Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters. You will be required to **select a SNV subtyping scheme** to use for your analysis.

![](images/bio_hansel-pipeline_launch-initial.png)

Please select the **Salmonella Heidelberg SNV Subtyping Scheme** and ensure that the **Save bio_hansel results to Project Line List Metadata** checkbox is checked:

![](images/bio_hansel-pipeline_launch-selected_scheme-save_to_sample_metadata.png)

You can leave the other parameters unmodified. Please use the **Ready to Launch?** button to start the pipeline.

![](images/launch.png)

Once the button is selected you should see a screen showing that your pipeline has been launched.


Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![](images/analysis.png)

The will bring you to a page where you can monitor the status of each launched workflow.

Clicking the pipeline name will bring you to a page for that analysis pipeline. 


Viewing Individual Sample Results
=================================

Once the pipeline is complete, you will be able to view the `bio_hansel` pipeline results and download the output files of the analysis.

![](images/bio_hansel-analysis-details-SRR1203042.png)

You can view the `bio_hansel` analysis output files:

![](images/bio_hansel-analysis_details-SRR1203042-output-preview.png)

You can view the detailed `bio_hansel` match results in a tabular view:

![](images/bio_hansel-analysis_details-preview-match_results.png)


Viewing Results For Multiple Samples
====================================

If you had checked the **Save bio_hansel results to Project Line List Metadata** checkbox on the `bio_hansel` pipeline launch page, you will be able to view the results of your analyses in the **Line List** table on the **Project** page:

![](images/bio_hansel-line_list_table-3-samples.png)


Interpreting the Results
========================

For more information on interpreting your `bio_hansel` results, please see:

- the [IRIDA bio_hansel Documentation][docs] or 
- the [bio_hansel GitHub] page.


[SRR1203042]: https://www.ebi.ac.uk/ena/data/view/SRR1203042&display=html
[forward reads]: ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR120/002/SRR1203042/SRR1203042_1.fastq.gz
[reverse reads]: ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR120/002/SRR1203042/SRR1203042_2.fastq.gz
[Web Upload Tutorial]: ../web-upload/
[docs]: ../../user/bio_hansel/
[bio_hansel GitHub]: https://github.com/phac-nml/bio_hansel
