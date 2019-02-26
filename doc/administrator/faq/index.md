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

Then the command [tbl2asn](//www.ncbi.nlm.nih.gov/genbank/tbl2asn2/) may need to be updated.  This can be done as follows:

1. Download the new `tbl2asn` binary from <ftp://ftp.ncbi.nih.gov/toolbox/ncbi_tools/converters/by_program/tbl2asn/>.
2. Copy the binary over the previously installed locations in Galaxy.  These can be found with the command:

        find galaxy/database/dependencies -iname 'tbl2asn'

    Where **galaxy/database/dependencies** is the location of all the installed Galaxy tool dependencies.

## 2. SPAdes Python version 3.6 not supported.

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

## 3. Can't locate `Bio/SeqIO.pm`

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

## 4. Installing conda dependencies in Galaxy versions < v16.01

IRIDA uses Galaxy versions >= v16.01 in order to take advantage of [conda dependency installation](https://docs.galaxyproject.org/en/master/admin/conda_faq.html).  However, it is still possible to integrate IRIDA with Galaxy versions < v16.01 with a bit of manual work to get the proper dependencies loaded up.  This involves loading up the necessary environment variables from a file, `galaxy/env.sh`, which is sourced before each tool is run.  The location of this file defaults to `galaxy/env.sh`, but can be changed with the **environment_setup_file** parameter in the Galaxy configuration file `galaxy/conf/galaxy.ini`.

An example of setting up the `sistr_cmd` dependency using this method is given below.  Please modify these steps for the particular tools in question (by e.g., installing different commands with conda, or adding appropriate binaries to the `PATH`).

### Step 1: Install `conda`

If `conda` is not already installed, please download and install <https://conda.io/miniconda.html>. Make sure to add the appropriate channels for installing software from bioconda:

```bash
conda config --add channels conda-forge
conda config --add channels defaults
conda config --add channels r
conda config --add channels bioconda
```

### Step 2: Install `sistr_cmd`

Install the `sistr_cmd` dependency to it's own conda environment:

```bash
conda create -y --name sistr_cmd@1.0.2 sistr_cmd=1.0.2
```

### Step 3: Write a wrapper around `sistr`

Write a wrapper around the `sistr` command to load up the conda environment.  If conda is installed in the directory `~/miniconda3` this should look like the following:

```bash
#!/bin/bash

export PATH=~/miniconda3/bin:$PATH
source activate sistr_cmd@1.0.2

sistr $@
```

Save this file with the name `sistr`.

### Step 4: Load up `sistr` wrapper during tool execution

Copy `sistr` to a directory loaded up by the `galaxy/env.sh` file.  For example, if this file contains the following:

```bash
export PATH=~/bin:$PATH
```

Then, copy `sistr` to `~/bin` and make executable.  Otherwise, adjust `env.sh` as necessary to put `sistr` on the `PATH`.

```bash
cp sistr ~/bin
chmod +x ~/bin/sistr
```

### Step 5: Test `sistr`

You can test out `sistr` by running as follows:

```bash
./bin/sistr --version
```

You should see `sistr_cmd 1.0.2` as output of the above command.

# IRIDA Web Install

## 1. MariaDB

MariaDB Ubuntu users may encounter errors when deploying IRIDA due to character set requirements. If the application does not launch and you see the following message in the IRIDA logs: 

```
Caused by: liquibase.exception.DatabaseException: Specified key was too long; max key length is 767 bytes [Failed SQL: CREATE TABLE irida_test.system_role (id BIGINT AUTO_INCREMENT NOT NULL, description VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, CONSTRAINT PK_SYSTEM_ROLE PRIMARY KEY (id), CONSTRAINT UK_3qbj4kdbey8f8wgabcel8i7io UNIQUE (name))]
```

you will need to make the following changes to MariaDB configurations files within the `/etc/mysql/mariadb.conf.d/` folder as described below:

__`50-mysql-clients.cnf`__

```
default-character-set = utf8
```

__`50-client.cnf`__

```
default-character-set = utf8
```

__`50-server.cnf`__

```
character-set-server = utf8
collation-server = utf8_general_ci
```

You will need to drop your databases, restart your mysql service, and then recreate your databases before re-running IRIDA for the changes to take effect.

