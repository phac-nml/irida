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

The data for this tutorial comes from <https://irida.corefacility.ca/downloads/snvphyl-galaxy/examples/snvphyl-example-lm.tar.gz>. It is assumed the sequence files (forward and reverse) `CFSAN002349` and `CFSAN023463` in fastq/ have been uploaded into appropriate samples as described in the [Web Upload Tutorial][]. Before starting this tutorial you should have a project with samples that appear as:

![snvphyl-samples]


Adding Samples to the Cart
==========================

Before a pipeline can be run, a set of samples and sequence read data must be selected and added to the cart. For this tutorial please select the  2 samples and click the **Add to Cart** button.

![add-to-cart]

Once the samples have been added to the cart, the samples can be reviewed by clicking on the **Cart** button at the top.

![cart-button]

Selecting a Pipeline
====================

For this tutorial, we will select the **SNVPhyl Phylogenomics Pipeline**.

![pipeline-select]


Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![snvphyl-pipeline-page][]

## Select a reference genome

SNVPhyl requires a reference genome to be used for mapping sequencing reads and calling variants.  This must be uploaded to the project containing the samples to use.  There is an example reference file in the sample data package (snvphyl-example-lm).  Please upload the file `CFSAN023463.fasta` by clicking the **Click or drag a new reference file to this area to upload.** button or by dragging and dropping the files in this area.

![snvphyl-pipeline-upload-ref-file][]

## Optional parameters

Next, selecting the **Modify** button brings up a page where parameters can be customized. The default parameters will often be appropriate, so we will use them for now.

![snvphyl-pipeline-customize][]

## Required parameters (density filtering)

One component of the SNVPhyl pipeline is to remove regions with high SNV density (which could suggest possible recombination). This component works well when all genomes under question are fairly closely-related, but when analyzing distantly-related genomes the **SNV density filtering** may remove too much data. The **SNV density filtering** can be enabled or disabled using the checkbox provided.

![snvphyl-pipeline-snv-density.png][]

Select the checkbox if all the genomes are closely-related to each other and you wish to remove SNVs in high-density regions (that could be indicative of recombination). The thresholds for SNV removal can be set in the [optional parameters][] section.

Leave the checkbox unchecked if you wish to turn off SNV density filtering. This is useful if you are analyzing genomes that are much more distantly related to each other (and so the SNV density filtering would be likely to remove non-recombinant SNVs).

More information on SNV density filtering can be found in the [SNVPhyl documentation][].

Running the pipeline
====================

Please select the **Launch Pipeline** button to start the pipeline.

![launch-button][]

Once the button is selected, you will be redirected to the pipeline details page.

Monitoring Pipeline Status
==========================

At any point, to monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![view-your-analyses][]

The will bring you to a page where you can monitor the status of each launched workflow.

![monitor-analyses][]

Clicking the pipeline name **SNVPhyl_20200702** will bring you to a page for that analysis pipeline. It will take a while for the SNVPhyl analysis pipeline to complete. Along the top of the page you can check the current step of the analysis and at the bottom of the **Details** tab you can select if you would like to receive an email upon pipeline completion or error. The email option is only available if the analysis is not in `COMPLETED` or `ERROR` state.

![analysis-in-progress][]

It will take a while the SNVPhyl analysis pipeline to complete.

Viewing the Results
===================

Once the pipeline is complete, you will see the generated phylogenetic tree within your browser.

![snvphyl-results][]

You can view the `SNVPhyl` analysis output files by selecting the `Output Files` tab. Note that not all files have an available preview and as such are not displayed in the Output File Preview but are downloaded when selecting the `Download All Files` button.

![snvphyl-output-files][]

To download individual files select the **...** next to the Download All Files and select the file to download.

![snvphyl-download-individual-files][]

To download al the files generated by the analysis, please select the **Download All Files** button.

![snvphyl-download-all-files][]


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

![snvphyl-provenance]

The provenance is displayed on a per file basis. Clicking on `filterStats.txt` file will display it's provenance. Expanding each tool will display the parameters that the tool was executed with.

![snvphyl-provenance-tools]


Viewing Pipeline Details
========================

To view analysis details, please select the **Settings** tab. From here you can view the analysis name, analysis description, analysis id, pipeline and pipeline version used by the analysis, analysis priority, when the analysis was created, and duration of the analysis.

![snvphyl-settings]

To edit an analysis name, please select the Pencil icon next to the analysis name. Once you have edited the analysis name, pressing the `ENTER` key on your keyboard or clicking anywhere outside of the text box will update the name. To cancel editing a name you can either hit the `ESC` key on your keyboard or if the name has not been changed you can also click anywhere outside of the text box.

![snvphyl-settings-edit-name]

To view samples used by the analysis, as well as, the reference file please select the **Samples** tab.

![snvphyl-settings-samples]

To share analysis results with other projects and/or save results back to samples, please select the **Manage Results** tab.

![snvphyl-settings-share]

To delete an analysis, please select the **Delete Analysis** tab.

![snvphyl-settings-delete]



Advanced SNVPhyl Visualizations
===============================

SNVPhyl Analyses can be combined with metadata from the sample the were run to get a more complete picture.  For more information see  [Advanced Visualizations].

To view the advanced visualization, click the **Phylogenetic Tree** tab and click the **View Advanced Visualization** button on the **Tree Preview** tab.

![snvphyl-results-adv-tree]


[add-to-cart]: images/add-to-cart.png
[Advanced Visualizations]: ../../../user/user/analysis-visualizations
[analysis-in-progress]: images/analysis-in-progress.png
[cart-button]: ../../../images/tutorials/common/pipelines/cart-button.png
[launch-button]: ../../../images/tutorials/common/pipelines/ready-to-launch-button.png
[monitor-analyses]: images/monitor-analyses.png
[pipeline-launched]: images/pipeline-launched.png
[pipeline-select]: images/pipeline-select.png
[SNVPhyl]: http://snvphyl.readthedocs.io/
[snvphyl-download-all-files]: images/snvphyl-download-all-files.png
[snvphyl-download-individual-files]: images/snvphyl-download-individual-files.png
[snvphyl-pipeline-customize]: images/snvphyl-pipeline-customize.png
[snvphyl-output-files]: images/snvphyl-output-files.png
[SNVPhyl Output Guide]: http://snvphyl.readthedocs.io/en/latest/user/output/
[snvphyl-pipeline-page]: images/snvphyl-pipeline-page.png
[snvphyl-pipeline-upload-ref-file]: images/snvphyl-pipeline-upload-ref-file.png
[snvphyl-provenance]: images/snvphyl-provenance.png
[snvphyl-provenance-tools]: images/snvphyl-provenance-tools.png
[snvphyl-results]: images/snvphyl-results.png
[snvphyl-results-adv-tree]: images/snvphyl-results-adv-tree.png
[snvphyl-samples]: images/snvphyl-samples.png
[snvphyl-settings]: images/snvphyl-settings.png
[snvphyl-settings-delete]: images/snvphyl-settings-delete.png
[snvphyl-settings-edit-name]: images/snvphyl-settings-edit-name.png
[snvphyl-settings-samples]: images/snvphyl-settings-samples.png
[snvphyl-settings-share]: images/snvphyl-settings-share.png
[snvphyl-pipeline-snv-density.png]: images/snvphyl-pipeline-snv-density.png
[view-your-analyses]: images/view-your-analyses.png
[Web Upload Tutorial]: ../web-upload/
[optional parameters]: #optional-parameters
[SNVPhyl documentation]: https://snvphyl.readthedocs.io/en/latest/user/parameters/#step-12-consolidate-vcfs
