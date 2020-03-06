---
layout: default
title: "Uploading with the IRIDA MiSeq Uploader Tool"
search_title: "Uploading with the IRIDA MiSeq Uploader Tool"
description: "A tutorial on how to upload sequencing data from an Illumina MiSeq instrument to IRIDA using the IRIDA MiSeq uploader tool."
---

Uploading with the Uploader Tool
================================
{:.no_toc}

This is a quick tutorial on how to upload data to IRIDA using the [IRIDA MiSeq Uploader Tool][uploader-tool]. You should use the IRIDA MiSeq uploader tool if you're uploading an entire MiSeq run to IRIDA. Usually you only want to use the uploader tool if you're a sequencing facility doing sequencing on behalf of a client.

This tutorial assumes that your client has already created the [project][project] where they want their data to reside in IRIDA. Clients must tell you what the numeric identifier for their project is so that you can use that information when setting up the run in the Illumina Experiment Manager.

* TOC
{:toc}

Downloading and Installing the Uploader
---------------------------------------
The Illumina MiSeq platform has a Windows 7 PC installed inside the sequencer. A Windows installer for the Illumina MiSeq uploader tool can be downloaded at: <https://github.com/phac-nml/irida-miseq-uploader/releases>

You can safely accept all default options once you launch the installer. 

### What does the installer install?

For complete transparency, the installer will install several components required by the uploader:

* Python 2.7.9 (32-bit)
* PyLauncher for Windows (<https://bitbucket.org/vinay.sajip/pylauncher>)
* The IRIDA MiSeq Uploader source code (<https://github.com/phac-nml/irida-miseq-uploader>)

### Command Line Uploader

Development is being done on a new Command Line Uploader.

If you would prefer using the command line to upload data, you can download it here <https://github.com/phac-nml/irida-uploader>

Please Note: The tutorial expects that you will be using the GUI uploader.

Launching the Uploader
----------------------

The installer will create a Start menu entry on your system:

![Start menu entry][start-menu-entry]

You can launch the uploader by clicking on the entry in the Start menu.

Configuring the Uploader
------------------------

### Creating a client in IRIDA

Before you can prepare the uploader to connect, you must create a client ID and secret in IRIDA.

{% include tutorials/clients/creating-a-client.md %}

**Important**: When you're creating a client entry for your uploader, you must select the `password` grant type, and you must select both the `read` and `write` scopes.

### Configuring the Uploader Settings

Once you've created the client ID and secret in the IRIDA web interface, you need to enter that information into the settings dialog of the uploader. Open the settings dialog by clicking on the **Options** menu and selecting **Settings**:

![Settings menu][settings-menu]

The default settings for the uploader will not work with your IRIDA install:

![Default settings dialog][default-settings-dialog]

* **Server URL**: The server URL is the location that the uploader should upload data to. If you navigate to your instance of IRIDA in your web browser, the URL (after you've logged in) will often look like: `https://irida.corefacility.ca/irida/`. The URL you should enter into the Server URL field is that URL, with `api/` at the end. So in the case of `https://irida.corefacility.ca/irida/`, you should enter the URL `https://irida.corefacility.ca/irida/api/`
* **Client authorization**
    * **ID**: This is the Client ID that you created when you [created a client](#creating-a-client-in-irida)
    * **Secret**: This is the Client Secret that was generated when you [created a client](#creating-a-client-in-irida)
* **User authorization**
    * **Username**: This is the username that you will use to connect to upload data to IRIDA. This user account must have the Sequencer or Administrator role.
    * **Password**: The password for the user account.
* **Completion command**: This is an arbitrary command that will be executed after an upload has finished (for example, you may want to use the Windows utility [Robocopy][robocopy] to copy the complete run folder to a different location on your network).

On successful connection, your settings dialog should have green checkmarks on each setting:

![Successful settings dialog][successful-settings-dialog]

Modifying the Sample Sheet
--------------------------

You must enter the numeric project ID number into the sample sheet for every sample that's going to be uploaded using the uploader tool. You can enter this number in the Illumina Experiment Manager tool in the `Sample Project` column:

![Illumina Experiment Manager][illumina-experiment-manager] 

You may also alter this value after the sequencer has completed the run using a spreadsheet tool like Microsoft Excel. Find the file named `SampleSheet.csv` in the directory containing the results of your experiment and open the file with Microsoft Excel. Enter the numeric project ID in the `Sample_Project` column for each sample in the list. Make sure that you save the file as a CSV file.

The uploader tool will create samples in the project(s) using the `Sample ID` field as the sample name. If a sample in the project already has a name in the `Sample ID` column (as might be the case with a top-up run), then the uploader will simply upload the new data to the existing sample.

Uploading Data
--------------

When you've finished [configuring the uploader](#configuring-the-uploader) and [modifying the sample sheet](#modifying-the-sample-sheet), you can proceed with upload.

[Launch the uploader](#launching-the-uploader) and choose the directory that contains your experiment results by clicking on the "Choose directory" button:

![Choose directory][choose-directory]

Use the dialog that appears to select the directory containing one or more sequencing run data directories, then click OK. You will see a success message indicating that the upload is ready to proceed if you've selected a directory that contains a valid `SampleSheet.csv` file:

![A valid directory is selected][valid-directory]

When you're ready to proceed with uploading, click on the "Upload" button at the bottom of the window. The upload will begin, and you will see the progress of individual files and the overall run indicated at the bottom of the window, along with an estimated time remaining before the upload is complete:

![Upload proceeding][upload-proceeding]

Depending on the size of your experiment and your connection speed to your instance of IRIDA, upload can take several minutes to hours of time.

[project]: ../../user/project/
[uploader-tool]: https://github.com/phac-nml/irida-miseq-uploader
[start-menu-entry]: {{ site.baseurl }}/images/tutorials/common/uploader-tool/start-menu-entry.png
[settings-menu]: {{ site.baseurl }}/images/tutorials/common/uploader-tool/settings-menu.png
[default-settings-dialog]: {{ site.baseurl }}/images/tutorials/common/uploader-tool/default-settings-dialog.png
[robocopy]: https://technet.microsoft.com/en-us/library/cc733145.aspx
[successful-settings-dialog]: {{ site.baseurl }}/images/tutorials/common/uploader-tool/successful-settings-dialog.png
[illumina-experiment-manager]: {{ site.baseurl }}/images/tutorials/common/uploader-tool/illumina-experiment-manager.png
[choose-directory]: {{ site.baseurl }}/images/tutorials/common/uploader-tool/choose-directory.png
[valid-directory]: {{ site.baseurl }}/images/tutorials/common/uploader-tool/valid-directory.png
[upload-proceeding]: {{ site.baseurl }}/images/tutorials/common/uploader-tool/upload-proceeding.gif
