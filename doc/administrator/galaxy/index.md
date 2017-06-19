---
layout: default
search_title: "Galaxy Setup"
description: "Galaxy install guide"
---

Galaxy Setup
============
{:.no_toc}

This document describes the necessary steps for the integration of [Galaxy][] with IRIDA as well as using Galaxy and [Galaxy ToolSheds][] to install workflows.  This is broken up into two sections:

1. [Integration with existing Galaxy][integration-galaxy]: If you already have a Galaxy instance installed, this will describe the necessary changes to the configuration needed in order for IRIDA to communicate with Galaxy and exsiting workflows.
2. [Setup of a new Galaxy instance][setup-new-galaxy]: If you do not have a Galaxy instance installed, this will walk through the general procedure of setting up a new Galaxy instance.

If you encounter errors while installing Galaxy you may want to look over the [IRIDA/Galaxy FAQ][].

IRIDA Galaxy Architecture
=========================

The overall architecture of IRIDA and Galaxy is as follows:

![irida-galaxy.jpg][]

1. IRIDA manages all input files for a workflow.  This includes sequencing reads, reference files, and the Galaxy workflow definition file.  On execution of a workflow, references to these files are sent to a Galaxy instance using the [Galaxy API][].  It is assumed that these files exist on a file system shared between IRIDA and Galaxy.
2. All tools used by a workflow are assumed to have been installed in Galaxy during the setup of IRIDA.  The Galaxy workflow is uploaded to Galaxy and the necessary tools are executed by Galaxy.  Galaxy can be setup to either execute tools on a local machine, or submit jobs to a cluster.
3. Once the workflow execution is complete, a copy of the results are downloaded into IRIDA and stored in the shared filesystem.

[Galaxy]: https://wiki.galaxyproject.org/FrontPage
[Galaxy API]: https://wiki.galaxyproject.org/Learn/API
[integration-galaxy]: existing-galaxy
[setup-new-galaxy]: setup
[Galaxy Toolsheds]: https://wiki.galaxyproject.org/ToolShed
[IRIDA/Galaxy FAQ]: ../faq
[irida-galaxy.jpg]: images/irida-galaxy.jpg
