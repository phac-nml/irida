---
layout: default
search_title: "FAQ"
description: "Frequently Asked Questions"
---

# Frequently Asked Questions

* This comment becomes a toc.
{:toc}

# Galaxy Install

## 1. `tbl2asn` out of date

If you see the following message when running Prokka.

```
[tbl2asn] This copy of tbl2asn is more than a year old.  Please download the current version.
[15:54:18] Could not run command: tbl2asn -V b -a r10k -l paired-ends -M n -N 1 -y 'Annotated using prokka 1.11 from http://www.vicbioinformatics.com' -Z outdir\/prokka\.err -i outdir\/prokka\.fsa 2> /dev/null
```

Then the command [tbl2asn](//www.ncbi.nlm.nih.gov/genbank/tbl2asn2/) may need to be updated.  This can be done as below.

1. Download the new `tbl2asn` binary from <ftp://ftp.ncbi.nih.gov/toolbox/ncbi_tools/converters/by_program/tbl2asn/>.
2. Copy the binary over the previously installed locations in Galaxy.  These can be found with the command:

        find galaxy/database/dependencies -iname 'tbl2asn'

    Where **galaxy/database/dependencies** is the location of all the installed Galaxy tool dependencies.

## 1. SPAdes Python version 3.6 not supported.

If you get a message like the following for SPAdes in Galaxy.

```
== Error ==  python version 3.6 is not supported!
Supported versions are 2.4, 2.5, 2.6, 2.7, 3.2, 3.3, 3.4, 3.5
```

Then you may need to set the Python version for the SPAdes instance.  If installed via conda, this can be done like:

```bash
source activate __spades@3.9.0
conda install python=3.5
```

Please run `conda info --envs` to see the exact name of the SPAdes conda environment.

## 2. Can't locate `Bio/SeqIO.pm`

If a message like the following appears for Galaxy tools.

```
Can't locate Bio/SeqIO.pm in @INC (you may need to install the Bio::SeqIO module) (@INC contains: /path/to/miniconda3/envs/__perl@_uv_/lib/perl5/site_perl/5.22.2/x86_64-linux-thread-multi ...
```

Then you may try uninstalling and re-installing the particular tool in Galaxy while monitoring the Galaxy log file (`main.log` or `paster.log`) to verify no error is occuring while installing the tool and all dependencies.

Alternatively, you may attempt to manually install bioperl in the tool environment.  Here, one of the paths in `@INC` is `miniconda3/envs/__perl@_uv_`, which means you can use this conda environment to install bioperl.  This can be done with.

```bash
source activate __perl@_uv_
conda install perl-bioperl
```

