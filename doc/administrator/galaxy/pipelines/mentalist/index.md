---
layout: default
search_title: "IRIDA MentaLiST MLST Analsyis"
description: "Install guide for the MentaLiST pipeline."
---

MentaLiST MLST Analysis
=======================

## Step 1: Galaxy Conda Setup

Galaxy makes use of [Conda][conda] to automatically install some dependencies for MentaLiST.  Please verify that the version of Galaxy is >= v16.01 and has been setup to use conda (by modifying the appropriate configuration settings, see [here][galaxy-config] for additional details).

## Step 2: Install Galaxy Tools

Please install all the `mentalist` Galaxy tool by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for `mentalist` and installing the appropriate **Toolshed Installable Revision**.

The install progress can be checked by monitoring the Galaxy log files `galaxy/*.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

## Step 3: Installing kmer Databases

![mentalist-download-pubmlst][]
    
[galaxy-config]: ../../setup#step-4-modify-configuration-file
[mentalist-download-pubmlst]: ../test/mentalist/images/mentalist-download-pubmlst.png