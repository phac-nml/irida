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
Before analyzing samples, we must prepare a MentaLiST kmer database for the organism of interest. This is done in the Galaxy web interface, and must be done with an account that has Galaxy Admin privileges. This step will only need to be done once per organism. A

1. On the Galaxy Admin page, select **Local Data** from the panel on the left side of the screen.
2. Click **MentaLiST Download from pubMLST**:

    ![mentalist-data-managers][] 

3. Enter **Salmonella enterica** into the **Select scheme to download** drop-down menu and leave **Kmer size** at the default value of **31**. Click **Execute** to create the Salmonella kmer database.

    ![mentalist-download-pubmlst-galaxy-tool][]
    
4. A green result box will appear in the galaxy history once the database is complete:

    ![mentalist-download-pubmlst-result][]
    
5. To confirm that the database is installed, select **mentalist_databases** from the list of available Tool Data Tables:

    ![mentalist-data-table-list][]
    
6. Your new MentaLiST database will be listed in the table of available MentaLiST databases. If it doesn't appear, click the refresh button at the top of the table.

    ![mentalist-database-available][]

Sample Data
============
The data for this tutorial comes from <https://irida.corefacility.ca/downloads/data/irida-sample-data.zip>. It is assumed the sequence files in `miseq-run-salmonella/` have been uploaded into appropriate samples as described in the Web Upload Tutorial. Before starting this tutorial you should have a project with samples that appear as:

![mentalist-tutorial-samples.png][]


[mentalist-github]: https://github.com/WGS-TB/MentaLiST
[mentalist-tutorial-samples.png]: images/mentalist-tutorial-samples.png
[mentalist-data-managers]: images/mentalist-data-managers.png
[mentalist-download-pubmlst-galaxy-tool]: images/mentalist-download-pubmlst-galaxy-tool.png
[mentalist-download-pubmlst-result]: images/mentalist-download-pubmlst-result.png
[mentalist-data-table-list]: images/mentalist-data-table-list.png
[mentalist-database-available]: images/mentalist-database-available.png