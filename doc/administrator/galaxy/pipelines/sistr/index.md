---
layout: default
search_title: "IRIDA SISTR Salmonella Typing"
description: "Install guide for the SISTR pipeline."
---

SISTR Typing
============

This workflow uses the software [sistr_cmd][] for typing of Salmonella genomes which are first assembled using [SPAdes][].  The specific Galaxy tools are listed in the table below.

| Tool Name                 | Tool Revision | Toolshed Installable Revision | Toolshed             |
|:-------------------------:|:-------------:|:-----------------------------:|:--------------------:|
| **flash**                 | 4287dd541327  | 0 (2015-05-05)                | [IRIDA Toolshed][]   |
| **filter_spades_repeats** | f9fc830fa47c  | 0 (2015-05-05)                | [IRIDA Toolshed][]   |
| **assemblystats**         | 51b76a5d78a5  | 1 (2015-05-07)                | [IRIDA Toolshed][]   |
| **spades**                | 21734680d921  | 14 (2015-02-27)               | [Galaxy Main Shed][] |
| **regex_find_replace**    | 9ea374bb0350  | 0 (2014-03-29)                | [Galaxy Main Shed][] |
| **sistr_cmd**             | 9d7e381dfa5a  | 1 (2017-03-03)                | [Galaxy Main Shed][] |

To install these tools please proceed through the following steps.

## Step 1: Install Dependencies

Some of these tools require additional dependencies to be installed.  For a cluster environment please make sure these are available on all cluster nodes by installing to a shared directory.

1. [gnuplot][]: Please download and install [gnuplot][] or make sure this is available in your execution environment.
2. **Perl Modules**: Please download and install dependency Perl modules with the command.

```bash
cpanm Time::Piece XML::Simple Data::Dumper
```

## Step 2.a: Conda dependenies (for Galaxy versions >= v16.01)

The SISTR pipeline makes use of the [conda][] package manager and [bioconda][] channel to distribute some dependencies, particularly the [sistr_cmd][] software.  It is **strongly** recommended to upgrade your Galaxy version (minimum >= v16.01, although > v16.07 is recommended) to take advantage of automated installation of dependencies using conda.

If the Galaxy version supports conda, then you must verify that Galaxy is setup to use conda for dependency installation.  This will primarly involve setting `conda_prefix` to point to the PATH of conda in your `config/galaxy.ini` file and verifying that conda will be used for dependency management in the file `config/dependency_resolvers_conf.xml`.  More details can be found at <https://docs.galaxyproject.org/en/master/admin/conda_faq.html>.

If conda is setup with your instance of Galaxy, please proceed to **Step 3**.  Otherwise, proceed to **Step 2.b**.

## Step 2.b: Conda dependenies (for Galaxy versions < v16.01)

If you are unable to upgrade Galaxy to take advantage of `conda`, then the following steps can be taken to get the `sistr_cmd` dependency working with an older version of Galaxy (by writing a wrapper to load up dependencies for `sistr_cmd` via conda).

1. If `conda` is not already installed, please download and install <https://conda.io/miniconda.html>.
2. Install the `sistr_cmd` dependency to it's own conda environment:

   ```bash
  conda create -y --name sistr_cmd@0.3.4 sistr_cmd=0.3.4 
   ```

3. Write a wrapper around the `sistr` command to load up the conda environment.  If conda is installed in the directory `~/miniconda2` this should look like the following:

   ```bash
   #!/bin/bash

   export PATH=~/miniconda2/bin:$PATH
   source activate sistr_cmd@0.3.4

   sistr $@
   ```

   Save this file with the name `sistr`.

4. Copy `sistr` to a directory loaded up by the [Galaxy environment](../../#galaxy-environment-setup).  For example, if `$GALAXY_ENV` is `~/env.sh` and contains the following:

   ```bash
   export PATH=~/bin:$PATH
   ```

   Then, copy `sistr` to `~/bin` and make executable.

   ```bash
   cp sistr ~/bin
   chmod +x ~/bin/sistr
   ```

5. Test `sistr`.  You can test out `sistr` by running as follows:

   ```bash
   ./bin/sistr --version
   ```

   You should see `sistr_cmd 0.3.4` as output of the above command.

## Step 3: Install Galaxy Tools

Please install all the Galaxy tools in the table above by logging into Galaxy, navigating to **Admin > Search and browse tool sheds**, searching for the appropriate **Tool Name** and installing the appropriate **Toolshed Installable Revision**.

The install progress can be checked by monitoring the Galaxy log file `$GALAXY_BASE_DIR/main.log`.  On completion you should see a message of `Installed` next to the tool when going to **Admin > Manage installed tool shed repositories**.

## Step 4: Testing Pipeline

A Galaxy workflow and some test data has been included with this documentation to verify that all tools are installed correctly.  To test this pipeline, please proceed through the following steps.

1. Upload the [SISTR Typing Galaxy Workflow][] by going to **Workflow > Upload or import workflow**.
2. Upload the sequence reads by going to **Analyze Data** and then clicking on the **upload files from disk** icon ![upload-icon][].  Select the [test/reads][] files.  Make sure to change the **Type** of each file from **Auto-detect** to **fastqsanger**.  When uploaded you should see the following in your history.

    ![upload-history][]

3. Construct a dataset collection of the paired-end reads by clicking the **Operations on multiple datasets** icon ![datasets-icon][].  Please check off the two **.fastq** files and then go to **For all selected... > Build List of dataset pairs**.  You should see a screen that looks as follows.

    ![dataset-pair-screen][]

4. This should have properly paired your data and named the sample **AE014613-699860**.  Enter the name of this paired dataset collection at the bottom and click **Create list**.
5. Run the uploaded workflow by clicking on **Workflow**, clicking on the name of the workflow **SISTR Analyze Reads v0.1 (imported from uploaded file)** and clicking **Run**.  This should auto fill in the dataset collection.  At the very bottom of the screen click **Run workflow**.
6. If everything was installed correctly, you should see each of the tools run successfully (turn green).  On completion this should look like.

    ![workflow-success][]

    If you see any tool turn red, you can click on the view details icon ![view-details-icon][] for more information.

If everything was successfull then all dependencies for this pipeline have been properly installed.

[SPAdes]: http://bioinf.spbau.ru/spades
[Galaxy Main Shed]: http://toolshed.g2.bx.psu.edu/
[IRIDA Toolshed]: https://irida.corefacility.ca/galaxy-shed
[gnuplot]: http://www.gnuplot.info/
[SISTR Typing Galaxy Workflow]: ../test/sistr/sistr.ga
[upload-icon]: ../test/snvphyl/images/upload-icon.jpg
[test/reads]: ../test/sistr/reads
[upload-history]: ../test/sistr/images/upload-history.png
[datasets-icon]: ../test/snvphyl/images/datasets-icon.jpg
[dataset-pair-screen]: ../test/sistr/images/dataset-pair-screen.png
[workflow-success]: ../test/sistr/images/workflow-success.png
[view-details-icon]: ../test/snvphyl/images/view-details-icon.jpg
[conda]: https://conda.io/docs/intro.html
[bioconda]: https://bioconda.github.io/
[sistr_cmd]: https://github.com/peterk87/sistr_cmd
