---
layout: default
search_title: "Setup of Galaxy for IRIDA"
description: "Galaxy install guide"
---

Setup of Galaxy for IRIDA
=========================
{:.no_toc}

These instructions describe the necessary steps for the integration of [Galaxy][] with IRIDA as well as using Galaxy and [Galaxy ToolSheds][] to install workflows.

Requirements
------------

Before proceeding with the integration of Galaxy and IRIDA, the following requirements need to be met.

1. [Galaxy version >= **v16.01**][galaxy-versions] is required as IRIDA makes use of [conda with Galaxy][].  Earlier versions were supported previously, but are being phased out as more required tools are released under conda.  A method to get newer conda-based tools to work with older Galaxy versions is described in our [FAQ][faq-conda], however this option will not be supported and is not recommended.
2. The filesystem is shared between the machines serving IRIDA and Galaxy under the same paths (e.g., `/path/to/irida-data` on the IRIDA server is available as `/path/to/irida-data` on the Galaxy server).

Quick Start
-----------

The easiest way to get Galaxy up and running for use with IRIDA is to use a custom-built [Docker][] image.  To start this image, please do the following:

```bash
# (Optional) Install Docker
# curl -sSL https://get.docker.com/ | sh

# Run IRIDA/Galaxy docker
docker run -d -p 48888:80 -v /path/to/irida/data:/path/to/irida/data phacnml/galaxy-irida-18.09
```

Where `48888` is the port on your local system where Galaxy should be accessible, and `/path/to/irida/data` should point to the location where the sequencing data for IRIDA is stored (i.e., the parent directory of `{sequence,reference,output}.file.base.directory` in [/etc/irida/irida.conf][irida-conf]).

Now proceed to installing the [IRIDA web interface][irida-web], making sure to set the following Galaxy connection parameters in `/etc/irida/irida.conf`.

```
galaxy.execution.url=http://localhost:48888
galaxy.execution.apiKey=admin
galaxy.execution.email=admin@galaxy.org
```

Detailed Instructions
---------------------

For more detailed instructions on installing Galaxy please refer to the following.

1. [Integration with existing Galaxy][integration-galaxy]: If you already have a Galaxy instance installed, this will describe the necessary changes to the configuration needed in order for IRIDA to communicate with Galaxy.
2. [Setup of a new Galaxy instance][setup-new-galaxy]: If you do not have a Galaxy instance installed, this will walk through the general procedure of setting up a new Galaxy instance.
3. [Automated Cleanup of Galaxy files][galaxy-cleanup]: Many intermediate files are produced when executing an IRIDA workflow in Galaxy. These instructions destribe setting up automated cleanup of these files.

If you encounter errors while installing Galaxy you may want to look over the [IRIDA/Galaxy FAQ][].

Architecture
------------

The overall architecture of IRIDA and Galaxy is as follows:

![irida-galaxy.jpg][]

1. IRIDA manages all input files for a workflow.  This includes sequencing reads, reference files, and the Galaxy workflow definition file.  On execution of a workflow, references to these files are sent to a Galaxy instance using the [Galaxy API][].  It is assumed that these files exist on a file system shared between IRIDA and Galaxy.
2. All tools used by a workflow are assumed to have been installed in Galaxy during the setup of IRIDA.  The Galaxy workflow is uploaded to Galaxy and the necessary tools are executed by Galaxy.  Galaxy can be setup to either execute tools on a local machine, or submit jobs to a cluster.
3. Once the workflow execution is complete, a copy of the results are downloaded into IRIDA and stored in the shared filesystem.

[Docker]: https://www.docker.com/
[irida-galaxy.jpg]: images/irida-galaxy.jpg
[Galaxy API]: https://wiki.galaxyproject.org/Learn/API
[Galaxy]: https://wiki.galaxyproject.org/FrontPage
[integration-galaxy]: existing-galaxy/
[setup-new-galaxy]: setup/
[Galaxy Toolsheds]: https://wiki.galaxyproject.org/ToolShed
[IRIDA/Galaxy FAQ]: ../faq
[conda with Galaxy]: https://docs.galaxyproject.org/en/master/admin/conda_faq.html
[galaxy-versions]: https://docs.galaxyproject.org/en/master/releases/index.html
[faq-conda]: ../faq/#installing-conda-dependencies-in-galaxy-versions--v1601
[irida-conf]: ../web/#core-configuration
[irida-web]: ../web/
[galaxy-cleanup]: cleanup/
