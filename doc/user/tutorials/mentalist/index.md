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

![mentalist-samples][]

Adding Samples to the Cart
==========================

Before a pipeline can be run a set of samples and sequence read data must be selected and added to the cart. For this tutorial please select the single sample and click the **Add to Cart** button.

![add-to-cart][]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button][]

Selecting a Pipeline
====================

Once inside the cart all available pipelines will be listed in the main area of the page.

![pipeline-select][]

For this tutorial, we will select the **MentaLiST MLST Pipeline**.

Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![mentalist-pipeline-page][]

Before launching the pipeline, we must select a MentaLiST kmer database to run our samples against. Select an appropriate 'Salmonella enterica' database from the **Kmer DB** drop-down menu. Note that the kmer databases available on your system will have a different date and may have been built with a different value of k than the ones shown below. If there is no 'Salmonella enterica' database available, please contact your IRIDA system administrator and refer to the [administrator documentation][mentalist-admin-docs] for detailed instructions on installing MentaLiST kmer databases. Use the **Ready to Launch?** button to start the pipeline.

![launch-button][]

Once the button is selected you should see a screen showing that your pipeline has been launched.

![pipeline-launched][]

Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu or click the **Let's see how this pipeline is doing** button.

![view-your-analyses][]

The will bring you to a page where you can monitor the status of each launched workflow.

![monitor-analysis][]

Clicking the pipeline name **MLSTMentalist_20200221** will bring you to a page for that analysis pipeline. This page will continue to refresh as the pipeline progresses through each stage.  It will take a while for the MentaLiST analysis pipeline to complete.

Viewing the Results
===================

Once the pipeline is complete, you will see the MentaLiST MLST results within your browser and you will be given the option to download the results of the analysis.

![mentalist-results][]

Interpreting the Results
========================

MentaLiST provides MLST results in a simple tab-separated value file that can be opened in a plaintext editor such as [Microsoft Notepad][microsoft-notepad] or [Apple TextEdit][apple-textedit], or a spreadsheet application such as [Microsoft Excel][microsoft-excel] or [LibreOffice Calc][libreoffice-calc].

The first row is a header. The following rows represent the MLST results for each sample. The first column contains the sample ID. Subsequent columns contain allele calls for each locus in the typing scheme. The sequence type (ST) and clonal complex are reported in the final two columns.

![mentalist-results-file]

Viewing Provenance Information
==============================

To view the pipeline provenance information, please select the **Provenance** tab.

![mentalist-provenance][]

The provenance is displayed on a per file basis. Clicking on the `mentalist_call.tsv` file will display it's provenance. Expanding each tool will display the parameters that the tool was executed with.

![mentalist-provenance-tools][]


Viewing Pipeline Details
========================

To view analysis details, please select the **Settings** tab. From here you can view the analysis name, analysis description, analysis id, pipeline and pipeline version used by the analysis, analysis priority, when the analysis was created, and duration of the analysis.

![mentalist-settings]

If the analysis is not in `COMPLETED` or `ERROR` state, you can update if you would like to receive an email upon pipeline completion.

![email-upon-completion]

To edit an analysis name, please select the **Pencil** icon next to the analysis name.

![mentalist-settings-edit-name]

Add the text `_01` to the end of the name and hit enter.

![mentalist-settings-edit-name-updated]

To view samples used by the analysis, please select the **Samples** tab.

![mentalist-settings-samples]

To share analysis results with other projects, please select the **Manage Results** tab.

![mentalist-manage-results]

To delete an analysis, please select the **Delete Analysis** tab.

![delete-analysis]


[add-to-cart]: images/add-to-cart.png
[apple-textedit]: https://en.wikipedia.org/wiki/TextEdit
[cart-button]: images/cart-button.png
[delete-analysis]: images/delete-analysis.png
[email-upon-completion]: ../../../images/tutorials/common/pipelines/email-upon-completion.png
[launch-button]: ../../../images/tutorials/common/pipelines/ready-to-launch-button.png
[libreoffice-calc]: https://www.libreoffice.org/discover/calc/
[mentalist-admin-docs]: ../../../administrator/galaxy/pipelines/mentalist
[mentalist-docs]: https://github.com/WGS-TB/MentaLiST/tree/mentalist_v0.1/docs
[mentalist-github]: https://github.com/WGS-TB/MentaLiST
[mentalist-manage-results]: images/mentalist-manage-results.png
[mentalist-paper]: http://mgen.microbiologyresearch.org/content/journal/mgen/10.1099/mgen.0.000146
[mentalist-pipeline-page]: images/mentalist-pipeline-page.png
[mentalist-provenance]: images/mentalist-provenance.png
[mentalist-provenance-tools]: images/mentalist-provenance-tools.png
[mentalist-results]: images/mentalist-results.png
[mentalist-results-file]: images/mentalist-results-file.png
[mentalist-samples]: images/mentalist-samples.png
[mentalist-settings]: images/mentalist-settings.png
[mentalist-settings-edit-name]: images/mentalist-settings-edit-name.png
[mentalist-settings-edit-name-updated]: images/mentalist-settings-name-updated.png
[mentalist-settings-samples]: images/mentalist-settings-samples.png
[microsoft-excel]: https://products.office.com/en-ca/excel
[microsoft-notepad]: https://en.wikipedia.org/wiki/Microsoft_Notepad
[monitor-analysis]: images/view-analysis-status.png
[pipeline-launched]: images/pipeline-launched.png
[pipeline-select]: images/pipeline-select.png
[view-your-analyses]: images/view-your-analyses.png