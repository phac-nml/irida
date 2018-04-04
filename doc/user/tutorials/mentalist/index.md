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

Sample Data
============
The data for this tutorial comes from <https://irida.corefacility.ca/downloads/data/irida-sample-data.zip>. It is assumed the sequence files in `miseq-run-salmonella/` have been uploaded into appropriate samples as described in the Web Upload Tutorial. Before starting this tutorial you should have a project with samples that appear as:

![mentalist-tutorial-samples.png][]


[mentalist-github]: https://github.com/WGS-TB/MentaLiST
[mentalist-admin-docs]: ../../../administrator/galaxy/pipelines/mentalist
[mentalist-tutorial-samples.png]: images/mentalist-tutorial-samples.png
[mentalist-data-managers]: images/mentalist-data-managers.png
