---
layout: default
title: "refseq_masher: What's in your sequence data?"
search_title: "refseq_masher: What's in your sequence data?"
description: "Using refseq_masher in IRIDA."
---

refseq_masher: What's in your sequence data?
============================================
{:.no_toc}

[refseq_masher] searches your sequence data against a [Mash] database of 54,925 [NCBI RefSeq Genomes] to find what NCBI RefSeq genomes match or are contained within your sequence data.

This tutorial will run [refseq_masher] against a publicly available reads set.  

* TOC
{:toc}

Get tutorial reads data 
------------------------------------------

Let's get the WGS data for `SRR1203042` from [EBI]:

> Illumina MiSeq paired end sequencing; Whole genome shotgun sequencing of Salmonella enterica subsp. enterica serovar Abony str. FNW19H84 by Illumina MiSeq


Download forward and reverse reads for `SRR1203042`:

- [SRR1203042_1.fastq.gz]
- [SRR1203042_2.fastq.gz] 


Load WGS for SRR1203042 into IRIDA
----------------------------------

Create Sample in IRIDA:

![new-sample]

![upload-fastqs]
 
Upload `SRR1203042_1.fastq.gz` and `SRR1203042_2.fastq.gz`:

![seq-uploaded]


Add Sample to Cart
------------------

Select sample "SRR1203042" and add it to your Cart:

![add-to-cart]


Select refseq_masher Pipeline
-----------------------------

You'll see the various Pipelines in IRIDA:

![select-pipeline]

Select the `refseq_masher` pipeline:

![card]


Configure and Launch refseq_masher Analysis
-------------------------------------------

![launch-masher]

Modify the parameters to run `refseq_masher` if desired:

![masher-params]

Click the "Launch" button to submit the analysis. 

![pipeline-submitted]

Click the "Let's see how this pipeline is doing" button to go to **Your Analyses** page to monitor your analysis status.


Monitoring Analysis Status
--------------------------

Go to the **Your Analyses** page by clicking on **Analysis > Your Analyses**:

![analyses-your-analyses]

In the **Your Analyses** table, you should see an entry like:

![pipeline-progress]

Clicking the analysis named **RefSeqMasherOnPairedReads_...** will bring you to a page that looks like:

![analysis-in-progress]

When the analysis has completed, it should look like:

![analysis-complete]

You should see 2 files under "Output Files":

- `SRR1203042-refseq-masher-matches.tsv` 
    - Top matching NCBI Genomes to sample `SRR1203042` 
- `SRR1203042-refseq-masher-contains.tsv`
    - NCBI Genomes contained in sample `SRR1203042`


Viewing and Interpreting the Results
------------------------------------

### Matches - `SRR1203042-refseq-masher-matches.tsv`

Below is the top result from `SRR1203042-refseq-masher-matches.tsv` transposed for readability:

| Field | Value |
| ==== + === |
| sample | SRR1203042 |
| top_taxonomy_name | Salmonella enterica subsp. enterica serovar Abony str. 0014 |
| distance | 0.00650877 |
| pvalue | 0 |
| matching | 328/400 |
| full_taxonomy | Bacteria; Proteobacteria; Gammaproteobacteria; Enterobacterales; Enterobacteriaceae; Salmonella; enterica; subsp. enterica; serovar Abony; str. 0014 |
| taxonomic_subspecies | Salmonella enterica subsp. enterica |
| taxonomic_species | Salmonella enterica |
| taxonomic_genus | Salmonella |
| taxonomic_family | Enterobacteriaceae |
| taxonomic_order | Enterobacterales |
| taxonomic_class | Gammaproteobacteria |
| taxonomic_phylum | Proteobacteria |
| taxonomic_superkingdom | Bacteria |
| subspecies | enterica |
| serovar | Abony |
| plasmid |  |
| bioproject | PRJNA224116 |
| biosample | SAMN01823751 |
| taxid | 1029983 |
| assembly_accession | GCF_000487615.2 |
| match_id | ./rcn/refseq-NZ-1029983-PRJNA224116-SAMN01823751-GCF_000487615.2-.-Salmonella_enterica_subsp._enterica_serovar_Abony_str._0014.fna |

Given that our sample, `SRR1203042`, was from an Illumina MiSeq sequencing run of strain `Salmonella enterica subsp. enterica serovar Abony str. FNW19H84`, the top `refseq_masher` result confirms that the WGS is *Salmonella enterica* subsp. enterica serovar Abony. 

*For more info on interpreting refseq_masher **matches** results, see [refseq_masher matches documentation][matches]*

### Contains - `SRR1203042-refseq-masher-contains.tsv`

> If a read set potentially has multiple genomes, it can be “screened” against the database to estimate how well each genome is contained in the read set.

- [Mash Screen] tutorial


Below are the first 5 rows and columns of `SRR1203042-refseq-masher-contains.tsv`:

| sample | top_taxonomy_name | identity | shared_hashes | median_multiplicity | 
| ====== + ================= + ======== + ============= + =================== |
| SRR1203042 | Salmonella enterica subsp. enterica serovar Typhimurium | 0.99953 | 397/400 | 36 | 
| SRR1203042 | Salmonella enterica subsp. enterica serovar Typhimurium | 0.997614 | 385/400 | 285 | 
| SRR1203042 | Salmonella enterica subsp. enterica serovar Typhimurium | 0.997614 | 385/400 | 285 | 
| SRR1203042 | Salmonella enterica subsp. enterica serovar Choleraesuis | 0.997614 | 385/400 | 284 | 
| SRR1203042 | Salmonella enterica subsp. enterica serovar Choleraesuis | 0.997614 | 385/400 | 284 | 

We can see that `SRR1203042` mostly contains k-mers belonging to *Salmonella enterica* subsp. enterica NCBI RefSeq Genomes. 

About some of the fields in `SRR1203042-refseq-masher-contains.tsv`: 

- `sample` - Your sample name 
- `identity` - Proportion of identical hashes or k-mers between your sample and an NCBI RefSeq Genome in the Mash Sketch database
- `shared_hashes` - Number of hashes shared between your sample and an NCBI RefSeq Genome in the Mash Sketch database
- `median_multiplicity` - "median multiplicity is computed for shared hashes, based on the number of observations of those hashes within the pool" (from `mash screen -h` with Mash v2.0)

*For more info on interpreting refseq_masher **contains** results, see [refseq_masher contains documentation][contains]* and the [Mash Screen] documentation.


[refseq_masher]: https://github.com/phac-nml/refseq_masher
[Mash]: https://genomebiology.biomedcentral.com/articles/10.1186/s13059-016-0997-x
[Mash Screen]: https://mash.readthedocs.io/en/latest/tutorials.html#screening-a-read-set-for-containment-of-refseq-genomes
[NCBI RefSeq Genomes]: https://www.ncbi.nlm.nih.gov/genome
[EBI]: https://www.ebi.ac.uk/ena/data/view/SRR1203042&display=html
[SRR1203042_1.fastq.gz]: ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR120/002/SRR1203042/SRR1203042_1.fastq.gz
[SRR1203042_2.fastq.gz]: ftp://ftp.sra.ebi.ac.uk/vol1/fastq/SRR120/002/SRR1203042/SRR1203042_2.fastq.gz
[matches]: https://github.com/phac-nml/refseq_masher#matches---find-the-closest-matching-ncbi-refseq-genomes-in-your-input-sequences
[contains]: https://github.com/phac-nml/refseq_masher#contains---find-what-ncbi-refseq-genomes-are-contained-in-your-input-sequences
[analyses-your-analyses]: images/analyses-your-analyses.png
[card]: images/card.png
[add-to-cart]: images/add-to-cart.gif
[analysis-complete]: images/analysis-complete.png
[analysis-in-progress]: images/analysis-in-progress.png
[launch-masher]: images/launch-masher.png
[masher-params]: images/masher-params.png
[new-sample]: images/new-sample.png
[pipeline-progress]: images/pipeline-progress.png
[pipeline-submitted]: images/pipeline-submitted.png
[select-pipeline]: images/select-pipeline.png
[seq-uploaded]: images/seq-uploaded.png
[upload-fastqs]: images/upload-fastqs.png
