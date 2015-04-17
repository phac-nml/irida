---
layout: default
search_title: "Integration of Galaxy with a Cluster"
description: "Guide for setting up galaxy with a cluster back-end."
---

Integration of Galaxy with a Cluster
====================================

IRIDA Galaxy Cluster Architecture
---------------------------------

The architecture of IRIDA and Galaxy using a cluster for job execution is as follows.

![irida-galaxy-cluster.jpg][]

1. IRIDA manages all input files for a workflow.  This includes sequencing reads, reference files, as well as the Galaxy workflow definition file.  It is assumed that these files exist on a file system shared between IRIDA and Galaxy.
2. All tools used by a workflow are assumed to have been installed in Galaxy during the setup of IRIDA.  The Galaxy workflow is uploaded to Galaxy and the necessary tools are executed by Galaxy on individual worker nodes within the cluster.
3. Once the workflow execution is complete a copy of the results are downloaded into IRIDA and stored in the shared filesystem.

In addition to the assumptions made in the [main installation guide][] the following assumption is made before proceeding.

1. The shared filesystem between IRIDA and Galaxy is also shared among all the cluster nodes used to execute jobs.

That is to say, the following directories and files from the default installation must be shared among all nodes in the cluster.

```bash
# The base directory to setup Galaxy.  For a clustered environment this must be shared across all nodes of the cluster.
GALAXY_BASE_DIR=/home/galaxy-irida

# The root directory for the Galaxy software
GALAXY_ROOT_DIR=$GALAXY_BASE_DIR/galaxy-dist

# A special environment file used by Galaxy and the tools
GALAXY_ENV=$GALAXY_BASE_DIR/env.sh
```

Additionally, the Galaxy web server machine will need to be the same OS install as the cluster nodes since tools are being built on the Galaxy web server machine but will need to be executed on the cluster nodes.  The Galaxy-specific documentation on installing to a cluster can be found in the [Galaxy Cluster][] documentation.  A few of the additional steps that need to be taken are as follows.

Configure Galaxy Web Server as a Cluster Submit Host
--------------------------------------------------

In order for Galaxy to submit jobs to a cluster it needs to be able to be configure as a submit host for this cluster and have the necessary tools installed.  Please refer to the [Galaxy Cluster][] documentation and your cluster administrator for more details on how to get this set up.

Install Galaxy Environment on the Cluster
-----------------------------------------

Many of the tools that are installed in Galaxy require Perl and the installation of different Perl modules.  For a cluster environment, these need to be setup and shared across the cluster.  For PerlBrew, please change `$PERLBREW_ROOT` to this shared filesystem and follow the instructions in [Galaxy Environment Setup][].

Setup Galaxy to Submit to Cluster
---------------------------------

The [Galaxy Cluster][] documentation contains information on how to configure Galaxy to submit jobs to a cluster.

[main installation guide]: README.md
[Galaxy Cluster]: https://wiki.galaxyproject.org/Admin/Config/Performance/Cluster
[Galaxy Environment Setup]: ../environment
[PerlBrew]: http://perlbrew.pl/
[irida-galaxy-cluster.jpg]: images/irida-galaxy-cluster.jpg
