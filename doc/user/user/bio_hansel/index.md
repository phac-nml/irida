---
layout: default
search_title: "Using the Bio_Hansel Pipeline"
description: "A guide on using the Bio_Hansel Pipeline."
---

# Using the Bio_Hansel Pipeline

## Pipeline Overview

The Bio_Hansel pipeline is implemented within IRIDA makes use of the following steps subtype Salmonella.

1. Paired-end read collections are sent to the bio_hansel workflow within Galaxy
2. Samples are analyzed by the bio_hansel tool.
3. Multiple result files are compiled, and a .json representation of the bio_hansel technician results is produced
4. The .json result is then used to display the tech information within the bio_hansel results page.

## Running the Pipeline

The Bio_Hansel pipeline is easily executed using the steps below.

1. Create Project.
2. Add samples to Project.
3. Add samples to Cart.
4. Select the bio_hansel pipeline.
5. Press run, and wait for the analysis to be finished.
6. View results within the Analyses page.

## Bio_Hansel Results

### Status of `PASS`

A successfull bio_hansel run (with status of `PASS`) should produce the following page as output.

![pass.png][]

### Error with the Pipeline

A bio_hansel run that fails will result in the following output.

![fail.png][]

### Report

Interpretation of the produced output is as follows:

#### Bio_Hansel Information

Basic information on the bio_hansel analysis results.

* **Sample**: The name of the sample used within this analysis.
* **Subtype**: The subtype calculated from the tool.
* **QC Status**: The quality control module result from the analysis.
* **QC Message**: The quality control message if there was a warning or error in the analysis.

[pass.png]: images/pass.png
[fail.png]: images/fail.png
