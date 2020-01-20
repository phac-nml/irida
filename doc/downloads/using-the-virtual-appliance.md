---
layout: default
search_title: "Using the Virtual Appliance"
description: "Instructions some basic instructions on how to use the IRIDA virtual appliance."
---

Using the Virtual Appliance
===========================
{:.no_toc}

The virtual appliance is currently configured to use 8 CPU cores and requires **at least** 8GB of RAM. You *may* reduce the number of CPU cores allocated to the virtual appliance, however, we do not recommend that you use any less than 4 CPU cores.

Once you start the virtual appliance, you can connect to IRIDA in several ways: using your web browser, using an uploader tool, or using SSH.

* TOC
{:toc}

Connecting with your web browser
--------------------------------

You can connect to the IRIDA web interface by opening your web browser and navigating to <http://localhost:48888/irida/>. The virtual appliance is configured to proxy traffic on port `48888` to port `80` in the virtual environment.

You can log into the web interface for the first time using the username `admin` and the password `password1`. You will be prompted to change the password the first time you log in.

You can see the Galaxy web interface by opening your web browser and navigating to <http://localhost:49999/>. The virtual appliance is configured to proxy traffic on port `49999` to port `9090` in the virtual environment. We encourage you to take a look around with Galaxy, but do not change any tools or configuration settings -- this instance of Galaxy is configured for internal workflow execution in IRIDA.

Connecting with an uploader tool
--------------------------------

Please visit the GitHub for the [IRIDA Uploader](https://github.com/phac-nml/irida-uploader) for more information about how to upload data to the IRIDA virtual appliance.

Connecting with an SSH client
-----------------------------

You can connect to the IRIDA virtual appliance using an SSH client like [Putty](http://www.chiark.greenend.org.uk/~sgtatham/putty/) or [MobaXterm](http://mobaxterm.mobatek.net/) on Windows, or the `ssh` client in a UNIX or UNIX-like environment.

The virtual appliance is configured to proxy traffic on port `42222` to port `22` in the virtual environment. The username for the virtual appliance is `vagrant` and the password is `vagrant`.

To connect to the IRIDA virtual appliance with the `ssh` command in a UNIX or UNIX-like environment:

    ssh -p 42222 vagrant@localhost

Virtual Appliance Configuration Scripts
---------------------------------------

The IRIDA Virtual Appliance is created using a tool called [packer](https://packer.io). You can download the scripts that were used to create the virtual appliance from our `git` repository:

<https://github.com/phac-nml/irida/tree/development/packer>

