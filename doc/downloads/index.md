---
layout: default
title: "Downloads"
search_title: "IRIDA Downloads"
description: "Links to download components of IRIDA"
---

Downloading IRIDA
=================
{:.no_toc}

You can find individual download links, and links to corresponding documentation in the sections below.

* This comment becomes the toc
{:toc}

IRIDA Virtual Appliance
-----------------------

Documentation for installing IRIDA can be found [here](../administrator/), but is time consuming and must be adjusted to the quirks of your environment. We provide a fully-configured virtual appliance that can be opened with [Oracle VirtualBox](https://www.virtualbox.org/) for demonstration purposes. You can download the complete package at:

<https://irida.corefacility.ca/downloads/virtual/irida-vm-virtualbox-latest.zip>

### Using the Virtual Appliance

The virtual appliance is currently configured to use 8 CPU cores and requires **at least** 8GB of RAM. You *may* reduce the number of CPU cores allocated to the virtual appliance, however, we do not recommend that you use any less than 4 CPU cores.

Once you start the virtual appliance, you can connect to IRIDA in several ways: using your web browser, using an uploader tool, or using SSH. Please see "[Using the Virtual Appliance](./using-the-virtual-appliance.html)" for more information about how to use the IRIDA virtual appliance.

IRIDA Web Interface
-------------------

The IRIDA web interface is distributed as a `war` archive. You can access the latest stable release of IRIDA at:

<https://github.com/phac-nml/irida/releases/latest>

You may also see all releases of the IRIDA web interface at:

<https://github.com/phac-nml/irida/releases>

The documentation for installing and configuring the IRIDA web interface (and Galaxy!) can be found at:

<http://irida.corefacility.ca/documentation/administrator/>

IRIDA MiSeq Uploader Tool
-------------------------

The IRIDA MiSeq Uploader Tool is used to submit complete Illumina MiSeq instrument runs to an instance of IRIDA. You can access the latest stable release of the IRIDA MiSeq Uploader Tool at:

<https://github.com/phac-nml/irida-miseq-uploader/releases/latest>

You may also see all releases of the IRIDA MiSeq Uploader Tool at:

<https://github.com/phac-nml/irida-miseq-uploader/releases>

The documentation for using the IRIDA MiSeq Uploader Tool is included in the package, but can also be found at:

<https://irida.corefacility.ca/documentation/user/tutorials/uploader-tool/>

Sample Data
-----------

Sample data for testing IRIDA can be found at:

<https://irida.corefacility.ca/downloads/data/irida-sample-data.zip>

The sample data package consists of *simulated* Illumina MiSeq read data for three publicly available Listeria Monocytogenes genomes. You can find more information about the simulated data at:

<https://irida.corefacility.ca/downloads/data/README.txt>
