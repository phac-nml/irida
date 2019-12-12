---
layout: default
title: "User Guides"
search_title: "User Documentation"
description: "User documentation index."
---

Learn IRIDA
===========
{:.no_toc}

The IRIDA platform is composed of several different applications. Most users of IRIDA will use the web interface for managing and organizing their sequencing data, and for launching analytical pipelines on their data. Some advanced users of IRIDA might want to export their data to a separate instance of Galaxy or directly to the Linux command-line for more in-depth analysis. Administrative users and laboratory technicians will run tools for adding data to IRIDA from sequencing instruments.

#### Quick Links
{:.no_toc}
**[User Guide][user]**

**[Administrator Guide][admin]**

* TOC
{:toc}

Tutorials
---------

The user and administrator guides provide a comprehensive overview of the entire IRIDA platform, but might be too detailed for trying to figure out how to accomplish some tasks. The tutorials section has some examples of common tasks that you might want to accomplish with IRIDA, like getting data into IRIDA, analyzing your data within IRIDA, and getting your data out of IRIDA.

### Getting data into IRIDA

You can load sequencing data into IRIDA in two different ways:

1. Using the [web interface][web-upload].
2. Using the [IRIDA Uploader application](https://github.com/phac-nml/irida-uploader-tutorial).

The web interface upload feature is useful if you only want to add data to IRIDA for one or two samples. If you want to load data into IRIDA in bulk (especially if you're a sequencing facility!) you should use the uploader application to transfer your data.

The IRIDA Uploader is also available as a Command Line tool via [bioconda](https://anaconda.org/bioconda/irida-uploader) and source code on [GitHub](https://github.com/phac-nml/irida-uploader)

### Analyzing your data with IRIDA

IRIDA has several different built-in pipelines for analyzing your data:

1. [Assembling your sequencing data][assembly].
2. [Whole-genome SNV Phylogeny][snvphyl].
3. [*Salmonella in-silico* Typing (SISTR)][sistr].
4. [refseq_masher - Find what NCBI RefSeq genomes match or are contained within your sequence data using Mash MinHash with a Mash sketch database of 54,925 NCBI RefSeq Genomes][refseq_masher].
5. [MentaLiST MLST][mentalist].
6. [Bio_Hansel][biohansel]

### Getting your data out of IRIDA

You will sometimes want to be able to get data *out* of IRIDA, if you want to share your data with an external collaborator, or if you want to run more in-depth analysis than what the analytical tools in IRIDA provide. You can use the tutorials below to get your data out of IRIDA:

1. [Sharing projects][sharing]
2. [Sharing *some* data from a project][sharing-some]
3. [Exporting data to Galaxy][export-to-galaxy]
4. [Exporting data to the command-line][export-to-command-line]
4. [Exporting data to NCBI's Sequence Read Archive][export-to-ncbi]


User Guide
----------

You can read through the comprehensive [user guide][user] to learn about all of the features that are built into IRIDA.

Administrator Guide
-------------------

If you are a system administrator or lab technician in a sequencing facility, you should read through the comprehensive [administrator guide][admin]. Some portions of the [user guide][user] are prerequisites for the administrator guide, including:

1. [System overview][system-overview]
2. [Logging in][logging-in]


[user]: user/
[admin]: administrator/
[web-upload]: tutorials/web-upload/
[assembly]: tutorials/assembly/
[snvphyl]: tutorials/snvphyl/
[sistr]: tutorials/sistr/
[biohansel]: tutorials/bio_hansel/
[refseq_masher]: tutorials/refseq_masher/
[mentalist]: tutorials/mentalist/
[sharing]: tutorials/sharing/
[sharing-some]: tutorials/sharing-some/
[export-to-galaxy]: tutorials/export-to-galaxy/
[export-to-command-line]: tutorials/export-to-command-line/
[export-to-ncbi]: tutorials/export-to-ncbi/
[system-overview]: user/system-overview/
[logging-in]: user/login/
