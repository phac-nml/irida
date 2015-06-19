---
layout: default
title: "Downloads"
search_title: "IRIDA Downloads"
description: "Links to download components of IRIDA"
---

Downloading IRIDA
=================
{:.no_toc}

All IRIDA downloads can be found at:

<https://irida.corefacility.ca/downloads>

You can find individual download links, and links to corresponding documentation in the sections below.

* This comment becomes the toc
{:toc}

IRIDA Virtual Appliance
-----------------------

The IRIDA install procedure is well documented, but is time consuming and must be adjusted to the quirks of your environment. We provide a fully-configured virtual appliance that can be opened with [Oracle VirtualBox](https://www.virtualbox.org/) for demonstration purposes. You can download the complete package at:

<https://irida.corefacility.ca/downloads/virtual/irida-vm-virtualbox-latest.zip>

### Using the Virtual Appliance

The virtual appliance is currently configured to use 8 CPU cores and requires **at least** 8GB of RAM. You *may* reduce the number of CPU cores allocated to the virtual appliance, however, we do not recommend that you use any less than 4 CPU cores.

Once you start the virtual appliance, you can connect to IRIDA in several ways: using your web browser, using an uploader tool, or using SSH.

#### Connecting with your web browser

You can connect to the IRIDA web interface by opening your web browser and navigating to <http://localhost:48888/irida/>. The virtual appliance is configured to proxy traffic on port `48888` to port `80` in the virtual environment.

You can see the Galaxy web interface by opening your web browser and navigating to <http://localhost:49999/>. The virtual appliance is configured to proxy traffic on port `49999` to port `9090` in the virtual environment. We encourage you to take a look around with Galaxy, but do not change any tools or configuration settings -- this instance of Galaxy is configured for internal workflow execution in IRIDA.

#### Connecting with an uploader tool

Please see [IRIDA MiSeq Uploader Tool](#irida-miseq-uploader-tool) below for more information about how to upload data to the IRIDA virtual appliance.

#### Connecting with an SSH client

You can connect to the IRIDA virtual appliance using an SSH client like [Putty](http://www.chiark.greenend.org.uk/~sgtatham/putty/) or [MobaXterm](http://mobaxterm.mobatek.net/) on Windows, or the `ssh` client in a UNIX or UNIX-like environment.

The virtual appliance is configured to proxy traffic on port `42222` to port `22` in the virtual environment. The username for the virtual appliance is `vagrant` and the password is `vagrant`.

To connect to the IRIDA virtual appliance with the `ssh` command in a UNIX or UNIX-like environment:

    ssh -p 42222 vagrant@localhost

### Virtual Appliance Configuration Scripts

The IRIDA Virtual Appliance is created using a tool called [packer](https://packer.io). You can download the scripts that were used to create the virtual appliance from our `git` repository:

<https://irida.corefacility.ca/gitlab/irida/irida/tree/development/packer>

IRIDA Web Interface
-------------------

The IRIDA web interface is distributed as a `war` archive. You can access the latest stable release of IRIDA at:

<https://irida.corefacility.ca/downloads/webapp/irida-latest.war>

You may also see all releases of the IRIDA web interface at:

<https://irida.corefacility.ca/downloads/webapp/>

The documentation for installing and configuring the IRIDA web interface (and Galaxy!) can be found at:

<http://irida.corefacility.ca/documentation/administrator/>

IRIDA MiSeq Uploader Tool
-------------------------

The IRIDA MiSeq Uploader Tool is used to submit complete Illumina MiSeq instrument runs to an instance of IRIDA. You can access the latest stable release of the IRIDA MiSeq Uploader Tool at:

<https://irida.corefacility.ca/downloads/tools/irida-tools-latest-bin.zip>

You may also see all releases of the IRIDA MiSeq Uploader Tool at:

<https://irida.corefacility.ca/downloads/tools>

The documentation for using the IRIDA MiSeq Uploader Tool is included in the package, but can also be found at:

<https://irida.corefacility.ca/documentation/administrator/uploader/>

Sample Data
-----------

Sample data for testing IRIDA can be found at:

<https://irida.corefacility.ca/downloads/data/irida-sample-data.zip>

The sample data package consists of *simulated* Illumina MiSeq read data for three publicly available Listeria Monocytogenes genomes. You can find more information about the simulated data at:

<https://irida.corefacility.ca/downloads/data/README.txt>
