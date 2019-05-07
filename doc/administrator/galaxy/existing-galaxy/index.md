---
layout: default
search_title: "Integration with an existing Galaxy"
description: "Integration with an existing Galaxy"
---

Integration with an existing Galaxy
===================================
{:.no_toc}

IRIDA can be setup to use an existing Galaxy installation assuming a few conditions are met:

1. Galaxy version >= **16.01** is required as IRIDA makes use of [conda with Galaxy][].  A method to get newer conda-based tools to work with older Galaxy versions is described in our [FAQ][faq-conda], however this option will not be supported and is not recommended.
2. The filesystem is shared between the machines serving IRIDA and Galaxy under the same paths (e.g., `/path/to/irida-data` on IRIDA is available as `/path/to/irida-data` on the Galaxy server).
3. Galaxy is setup to use [PostgreSQL][] or [MySQL/MariaDB][] as it's database.  The default installation uses SQLite, but this is insufficient for the complex workflows used by IRIDA.
4. Some modifications to the configuration settings (see below) are made to enable IRIDA to communicate with Galaxy.

The following describes the procedures needed to get IRIDA setup with an existing Galaxy installation.

* This comment becomes the table of contents.
{:toc}

Dependencies
------------

Some tools will need to be built from source, and so require the standard Linux build environment to be installed on the Galaxy server. For CentOS this will include the following packages:

```bash
yum groupinstall "Development tools"
yum install mercurial zlib-devel ncurses-devel tcsh git db4-devel expat-devel java
```

Additionally, some tools assume certain dependencies are installed on the machines where the tools are to be run and do not install these dependencies automatically.  To handle these cases, you can create a specific conda environment which is loaded up each time a tool is run (via the `env.sh` file).  Assuming [conda is installed][Conda] these dependencies can be installed into a conda environment, **galaxy**, with:

```bash
conda create --name galaxy samtools perl-xml-simple perl-time-piece perl-bioperl openjdk gnuplot libjpeg-turbo
```

To load up this conda environment before each tool is run, please add the following to the `galaxy/env.sh` file:

```
source activate galaxy
```

Some Galaxy tool installation instructions for IRIDA may require the installation of additional dependencies which can be added to this conda **galaxy** environment.

*Note: the location of `galaxy/env.sh` is defined by the property `environment_setup_file` in `config/galaxy.ini`.*

Configuration settings
-----------------------

### Settings in `config/galaxy.ini`

The following is a list of other necessary configuration settings within the file `config/galaxy.ini` for IRIDA to function with Galaxy.

1. Change `allow_library_path_paste` to allow direct linking of files in Galaxy to the IRIDA file locations (as opposed to making copies). E.g.,
   * Change `#allow_library_path_paste = False` to `allow_library_path_paste = True`.
2. Set the Galaxy `id_secret` for encoding database ids. E.g.,
   * Change `#id_secret = USING THE DEFAULT IS NOT SECURE!` to `id_secret = some secure password`
      * The command `pwgen --secure -N 1 56` may be useful for picking a hard-to-guess key.
      * ***Note: Once this key is set, please do not change it.  This key is used to translate database ids in Galaxy to API ids used by IRIDA to access datasets, histories, and workflows.  IRIDA does store some of these API ids internally for debugging and tracking purposes and changing this value will render any of the API ids stored in IRIDA useless.***
3. Setup [Conda][] for installing tool dependencies via Galaxy's automated dependency management system. E.g.,
   * Set `conda_prefix = /home/galaxy-irida/miniconda3`, or wherever conda is installed for Galaxy.
   * Set `conda_ensure_channels = iuc,bioconda,r,defaults,conda-forge`.
4. Add Galaxy/IRIDA user to `admin_users` list (see below).

Setup of a Galaxy user and API key
----------------------------------

As IRIDA communicates with Galaxy through the [API][galaxy-api] it is necessary to setup a Galaxy API key which will be used by IRIDA. It is recommended (though not required) to use a separate user for this purpose.

This user must also have Galaxy admin privileges in order to allow IRIDA to link directly to the sequence files when sharing with Galaxy (avoids creating copies of fastq files each time a workflow is run). Admin privileges can be assigned by adding the user's email to `admin_users` in the configuration file `config/galaxy.ini`.

Setup IRIDA tools in Galaxy
---------------------------

### Step 1: Configure External Toolsheds

The workflows used by IRIDA make use of external tools that can be installed using a [Galaxy Toolshed][].  The two toolsheds used by IRIDA are the [Main Galaxy Toolshed][] and the [IRIDA Toolshed][].  These are configured in the file `config/tool_sheds_conf.xml`.  Please make sure the following two `tool_shed` entries are setup within this file:

```xml
<tool_sheds>
	<tool_shed name="Galaxy main tool shed" url="http://toolshed.g2.bx.psu.edu/"/>
	<tool_shed name="IRIDA Galaxy Toolshed" url="https://irida.corefacility.ca/galaxy-shed"/>
</tool_sheds>
```

### Step 2: Install Pipeline Tools

#### Automated installation of tools

{%include administrator/galaxy/setup/automated-tool-install.md %}

#### Manual installation of tools

Alternatively, the necessary tools can be installed manually through the following instructions specific to each pipeline in IRIDA:

* [SNVPhyl Whole Genome Phylogeny][]
* [Assembly and Annotation][]
* [Assembly and Annotation Collection][]
* [SISTR Salmonella Typing][]
* [refseq_masher]
* [MentaLiST MLST][]
* [Bio_Hansel][]

Link up Galaxy with IRIDA
-------------------------

In order to connect IRIDA to this Galaxy instance you will need to modify the parameters **galaxy.execution.url**, **galaxy.execution.email**, and **galaxy.execution.apikey** in the file `/etc/irida/irida.conf` and restart IRIDA. Additional details for configuring IRIDA can be found in the instructions to [install and configure the IRIDA web interface][web].

Once you have configured IRIDA to connect to Galaxy you can attempt to execute a workflow by adding some data to your cart, selecting  **Pipelines** from the main menu, then selecting a particular pipeline.  You will have to have some data uploaded into IRIDA before testing.  An example set of data can be found at [irida-sample-data.zip][].  Currently all workflows assume you are using paired-end sequence reads.

[conda with Galaxy]: https://docs.galaxyproject.org/en/master/admin/conda_faq.html
[Conda]: https://conda.io/miniconda.html
[galaxy-api]: https://wiki.galaxyproject.org/Learn/API
[PostgreSQL]: http://www.postgresql.org/
[IRIDA Toolshed]: https://irida.corefacility.ca/galaxy-shed
[Main Galaxy Toolshed]: https://toolshed.g2.bx.psu.edu/
[Galaxy Toolshed]: https://wiki.galaxyproject.org/ToolShed
[SNVPhyl Whole Genome Phylogeny]: ../pipelines/phylogenomics/
[SISTR Salmonella Typing]: ../pipelines/sistr/
[Assembly and Annotation]: ../pipelines/assembly-annotation/
[Assembly and Annotation Collection]: ../pipelines/assembly-annotation-collection/
[refseq_masher]: ../pipelines/refseq_masher/
[MentaLiST MLST]: ../pipelines/mentalist/
[Bio_Hansel]: ../pipelines/bio_hansel/
[Purging Histories and Datasets]: https://galaxyproject.org/admin/config/performance/purge-histories-and-datasets/
[PostgreSQL]: https://www.postgresql.org/
[MySQL/MariaDB]: https://mariadb.org/
[irida-sample-data.zip]: https://irida.corefacility.ca/downloads/data/irida-sample-data.zip
[faq-conda]: ../../faq/#installing-conda-dependencies-in-galaxy-versions--v1601
[web]: ../../web
