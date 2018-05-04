---
layout: default
search_title: "Using the Bio_Hansel Pipeline"
description: "A guide on using the Bio_Hansel Pipeline."
---

# Using the Bio_Hansel Pipeline

## Pipeline Overview

This guide describes the [Bio_Hansel][biohansel] pipeline within IRIDA. This pipeline enables rapid subtyping of *Salmonella enterica* subsp. enterica serovar Heidelberg and Enteritidis genomes using in-silico 33 bp k-mer SNP subtyping schemes. Input is provided in the form of sequence reads (in fastq format) and **Bio_Hansel** will produce output files containing the particular SNP subtypes found in the reads as well as data quality information to help guide interpretation of the results.

## Running the Pipeline

Details on running **Bio_Hansel** please see the [IRIDA/Bio_Hansel Tutorial][biohansel-tutorial]. 

## Bio_Hansel Results

The Bio_Hansel results page in IRIDA will look like the following.

![biohansel-results.png][]

Results are searchable (using the **Search** text box). Interpretation of the produced output is as follows:

* **Sample**: The name of the sample used within this analysis.
* **Subtype**: The subtype calculated from the tool.
* **QC Status**: The quality control module result from the analysis, either `PASS` or `FAIL`.
* **QC Message**: The quality control message if there was a warning or error in the analysis.

[biohansel]: https://github.com/phac-nml/bio_hansel
[biohansel-tutorial]: ../../tutorials/bio_hansel/
[biohansel-results.png]: images/biohansel-results.png