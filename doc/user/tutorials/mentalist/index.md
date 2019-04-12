---
layout: default
title: "MentaLiST: MLST Analysis"
search_title: "MentaLiST: MLST Analysis"
description: "A tutorial on how to type data with MentaLiST."
---

Multi-locus Sequence Typing with MentaLiST
==========================================
{:.no_toc}

This is a quick tutorial on how to use IRIDA to analyze data with [MentaLiST][mentalist-github].

* TOC
{:toc}

Prepare Kmer Database
=====================
Before analyzing samples, we must prepare a MentaLiST kmer database for the organism of interest. This is done in the Galaxy web interface, and must be done with an account that has Galaxy Admin privileges. This step will only need to be done once per organism, then subsequent analyses can re-use the same kmer database. Please refer to the [administrator documentation][mentalist-admin-docs] for detailed instructions on installing MentaLiST kmer databases.

Initial Data
============
The data for this tutorial comes from <https://irida.corefacility.ca/downloads/data/irida-sample-data.zip>. It is assumed the sequence files in `miseq-run-salmonella/` have been uploaded into appropriate samples as described in the Web Upload Tutorial. Before starting this tutorial you should have a project with samples that appear as:

![mentalist-tutorial-samples.png][]

Adding Samples to the Cart
==========================

Before a pipeline can be run a set of samples and sequence read data must be selected and added to the cart. For this tutorial please select the single sample and click the **Add to Cart** button.

![add-to-cart.png][]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button.png][]

Selecting a Pipeline
====================

Once inside the cart, you can select a pipeline from the available pipeline grid:

![select-a-pipeline-view.png][]

For this tutorial, we will select the **MentaLiST MLST Pipeline**.

Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![mentalist-pipeline-page.png][]

Before launching the pipeline, we must select a MentaLiST kmer database to run our samples against. Select an appropriate 'Salmonella enterica' database from the **Kmer DB** drop-down menu. Note that the kmer databases available on your system will have a different date and may have been built with a different value of k than the ones shown below. If there is no 'Salmonella enterica' database available, please contact your IRIDA system administrator and refer to the [administrator documentation][mentalist-admin-docs] for detailed instructions on installing MentaLiST kmer databases. Use the **Ready to Launch?** button to start the pipeline.

![ready-to-launch-button.png][]

Once the button is selected you should see a screen showing that your pipeline has been launched.

![pipeline-launch.png][]

Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![your-analyses-menu.png][]

The will bring you to a page where you can monitor the status of each launched workflow.

![your-analyses-page.png][]

Clicking the pipeline name **MLSTMentalist_20180404** will bring you to a page for that analysis pipeline. This page will continue to refresh as the pipeline progresses through each stage.  It will take a while for the MentaLiST analysis pipeline to complete.

Viewing the Results
===================

Once the pipeline is complete, you will see the MentaLiST MLST results within your browser and you will be given the option to download the results of the analysis.

![mentalist-results.png][]

Interpreting the Results
========================

MentaLiST provides MLST results in a simple tab-separated value file that can be opened in a plaintext editor such as [Microsoft Notepad][microsoft-notepad] or [Apple TextEdit][apple-textedit], or a spreadsheet application such as [Microsoft Excel][microsoft-excel] or [LibreOffice Calc][libreoffice-calc].

The first row is a header. The following rows represent the MLST results for each sample. The first column contains the sample ID. Subsequent columns contain allele calls for each locus in the typing scheme. The sequence type (ST) and clonal complex are reported in the final two columns.

![mentalist-results-file.png]

Viewing Provenance Information
==============================

To view the pipeline provenance information, please select the **Provenance** tab.

![mentalist-provenance.png][]

This will display the individual steps of this pipeline and the parameters used at each step.

[mentalist-github]: https://github.com/WGS-TB/MentaLiST
[mentalist-admin-docs]: ../../../administrator/galaxy/pipelines/mentalist
[mentalist-tutorial-samples.png]: images/mentalist-tutorial-samples.png
[mentalist-data-managers.png]: images/mentalist-data-managers.png
[add-to-cart.png]: images/add-to-cart.png
[cart-button.png]: images/cart-button.png
[select-a-pipeline.png]: images/select-a-pipeline.png
[select-a-pipeline-view.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/select-a-pipeline-view.png
[mentalist-pipeline-page.png]: images/mentalist-pipeline-page.png
[ready-to-launch-button.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/ready-to-launch-button.png
[pipeline-launch.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/pipeline-launch.png
[your-analyses-menu.png]: {{ site.baseurl }}/images/tutorials/common/pipelines/your-analyses-menu.png
[your-analyses-page.png]: images/your-analyses-page.png
[mentalist-results.png]: images/mentalist-results.png
[mentalist-results-file.png]: images/mentalist-results-file.png
[mentalist-provenance.png]: images/mentalist-provenance.png
[microsoft-notepad]: https://en.wikipedia.org/wiki/Microsoft_Notepad
[apple-textedit]: https://en.wikipedia.org/wiki/TextEdit
[microsoft-excel]: https://products.office.com/en-ca/excel
[libreoffice-calc]: https://www.libreoffice.org/discover/calc/
[mentalist-docs]: https://github.com/WGS-TB/MentaLiST/tree/mentalist_v0.1/docs
[mentalist-paper]: http://mgen.microbiologyresearch.org/content/journal/mgen/10.1099/mgen.0.000146
