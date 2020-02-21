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

The data for this tutorial comes from https://irida.corefacility.ca/downloads/data/irida-sample-data.zip. It is assumed the sequence files in miseq-run-assembly-small/ have been uploaded into appropriate samples as described in the [Web Upload Tutorial][]. Before starting this tutorial you should have a project with samples that appear as:

![snvphyl-samples]


Adding Samples to the Cart
==========================

Before a pipeline can be run a set of samples and sequence read data must be selected and added to the cart. For this tutorial please select all 3 samples and click the **Add to Cart** button.

![add-to-cart]

Selecting a Pipeline
====================

![pipeline-select]

For this tutorial, we will select the **SNVPhyl Phylogenomics Pipeline**.

Selecting Parameters
====================

Once the pipeline is selected, the next page provides an overview of all the input files, as well as the option to modify parameters.

![snvphyl-pipeline-page][]

SNVPhyl requires a reference genome to be used for mapping sequencing reads and calling variants.  This must be uploaded to the project containing the samples to use.  A number of example reference files are provided under the `references/` folder in the sample data package.  Please upload the file `08-5578.fasta` using the following steps.

![snvpyhyl-pipeline-upload-ref-file]

Selecting **Customize** brings up a page where parameters can be customized.

![snvphyl-customize][]

The default parameters will often be appropriate but we will modify the **Minimum read coverage** to `10` for this tutorial.  When finished please select **Use these Parameters**.

![min-read-coverage]

![min-read-coverage-modified]


Once a set of parameters has been chosen, the **Ready to Launch?** button may be used to start the pipeline.

![launch-button][]

Once the button is selected you should see a screen showing that your pipeline has been launched.

![pipeline-launched][]

Monitoring Pipeline Status
==========================

To monitor the status of the launched pipeline, please select the **Analysis > Your Analyses** menu.

![view-your-analyses][]

The will bring you to a page where you can monitor the status of each launched workflow.

![monitor-analyses][]

Clicking the pipeline name **SNVPhyl_20200221** will bring you to a page for that analysis pipeline.

![analysis-in-progress][]

It will take a while the SNVPhyl analysis pipeline to complete.

When the analysis has completed, it should look like:

![snvphyl-results]

Viewing the Results
===================

Once the pipeline is complete, you will see the generated phylogenetic tree within your browser and you will be given the option to download the results of the analysis.  Please click **Download** to download these results now.

![snvphyl-results.png][]

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

The provenance is displayed on a per file basis. Clicking on `mappingQuality.txt` file will display it's provenance. Expanding each tool will display the parameters that the tool was executed with.

![snvphyl-provenance-tools]


Viewing Pipeline Details
========================

To view analysis details, please select the **Settings** tab.

![snvphyl-settings]

To edit an analysis name, please select the **Pencil** icon next to the analysis name.

![snvphyl-settings-edit-name]

Add the text `_01` to the end of the name and hit enter.

![snvphyl-settings-edit-name-updated]

To view samples used by the analysis, as well as, the reference file please select the **Samples** tab.

![snvphyl-settings-samples]

To share analysis results with other projects and/or save results back to samples, please select the **Manage Results** tab.

![snvphyl-settings-manage-results]

To delete an analysis, please select the **Delete Analysis** tab.

![delete-analysis]



[add-to-cart]: images/add-to-cart.png
[analysis-in-progress]: images/analysis-in-progress.png
[delete-analysis]: images/delete-analysis.png
[launch-button]: images/launch-button.png
[min-read-coverage]: images/min-read-coverage.png
[min-read-coverage-modified]: images/min-read-coverage-modified.png
[monitor-analyses]: images/monitor-analyses.png
[pipeline-launched]: images/pipeline-launched.png
[pipeline-select]: images/pipeline-select.png
[SNVPhyl]: http://snvphyl.readthedocs.io/
[snvphyl-customize]: images/snvphyl-customize.png
[snvphyl-customize-parameters]: images/snvphyl-parameters.png
[SNVPhyl Output Guide]: http://snvphyl.readthedocs.io/en/latest/user/output/
[snvphyl-pipeline-page]: images/snvphyl-pipeline-page.png
[snvpyhyl-pipeline-upload-ref-file]: images/snvpyhyl-pipeline-upload-ref-file.png
[snvphyl-samples]: images/snvphyl-samples.png
[snvphyl-settings]: images/snvphyl-settings.png
[snvphyl-settings-edit-name]: images/snvphyl-settings-edit-name.png
[snvphyl-settings-edit-name-updated]: images/snvphyl-settings-edit-name-updated.png
[snvphyl-settings-samples]: images/snvphyl-settings-samples.png
[snvphyl-settings-manage-results]: images/snvphyl-settings-manage-results.png
[view-your-analyses]: images/view-your-analyses.png




[snvphyl-provenance.png]: images/snvphyl-provenance.png

Advanced SNVPHyl Visualizations
===============================

SNVPHyl Analyses can be combined with metadata from the sample the were run to get a more complete picture.  For more information see  [Advanced Visualizations]({{ site.baseurl }}/user/user/analysis-visualizations).
