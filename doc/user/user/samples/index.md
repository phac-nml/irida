---
layout: default
search_title: "Managing Samples"
description: "Documentation for managing samples in IRIDA."
---

Managing Samples
================
{:.no_toc}

Each [project](../project) in IRIDA may contain a collection of samples that corresponds to an isolate. Each sample may contain several sequencing files, either paired-end, single-end, or both. This section of the user guide describes how you can view samples, manage samples (merging, copying, renaming, exporting), and search for samples by name.

* This comment becomes the toc
{:toc}

Viewing samples in a project
----------------------------

{% include tutorials/common/samples/viewing-samples.md %}

The samples listing shows high-level sample details, such as:

* The name of the sample,
* The user-defined organism of the sample (if provided),
* The name of the person who collected the sample (if provided),
* The project that the sample belongs to (if from a related project),
* The date that the sample was created in IRIDA.

### Viewing individual sample details

All of the sample details that are in IRIDA are currently provided by a user with the project **Manager** role. To view details about an individual sample, start by [viewing the samples in a project](#viewing-samples-in-a-project), then click on the sample name in the samples table:

![Sample name button.]({{ site.baseurl }}/images/tutorials/common/samples/sample-name-button.png)

The sample details page shows all of the details that are currently known about a sample:

![Sample details page.]({{ site.baseurl }}/images/tutorials/common/samples/sample-details.png)

### Editing sample details

Start by [viewing the details of an individual sample](#viewing-individual-sample-details). On the samples details page, click on the "Edit" button in the top, right-hand corner:

![Sample details edit button.](images/sample-details-edit.png)

You can provide as many or as few sample details that you want -- the sample details are not used by any workflows in IRIDA (except the sample name in the SNVPhyl workflow), and (with the exception of the sample name) none of the sample details are required fields. When you've finished updating the sample details, you can click on the "Update" button at the bottom, right-hand side of the page.

### Viewing sequence files

{% include tutorials/common/samples/view-sequence-files.md %}

#### Uploading Sequence Files

{% include tutorials/common/uploading-sample-files.md %}

#### Downloading a sequence file

{% include tutorials/common/samples/download-sequence-file.md %}

You can download all sequence files in a sample by following the instructions in the [exporting samples](#exporting-samples) section about [downloading samples](#downloading-samples).

#### Deleting a sequence file

If you need to delete a sequence file from IRIDA, you can do so by clicking on the <img src="images/delete-icon.png" alt="Delete icon" class="inline"> icon, on the right-hand side of the row for the sequence file.

You can only delete a sequence file from a sample if you have the project <img src="images/manager-icon.png" class="inline" alt="Manager role icon."> **Manager** role on the project.

#### Viewing automated assemblies

If the project manager has enabled automated assemblies for uploaded data an assembly may be shown for a sequence file.

![Automated assembly](images/automated-assembly.png)

The assembly status will be displayed along with a link to view the assembly results page.  For more information on viewing pipeline results see the [pipeline documentation](../pipelines/#viewing-pipeline-results)  

See the [project documentation](../project#managing-automated-assemblies) for information on enabling automated assembly.

Adding a new sample
-------------------

{% include tutorials/common/creating-a-sample.md %}

Searching and filtering samples
-------------------------------

You can search and filter samples in a project in IRIDA by sample name, organism, and/or date range using the filters at the top of the [samples list](#viewing-samples-in-a-project):

![Samples filter area.](images/sample-filter-area.png)

### Search Field

![Samples search input.](images/search-input.png)

You can perform a general search on sample names using the search field.  This will filter samples that have the search string *anywhere* in the name or organism field.  So, for example, if you're searching for a sample that has the numeral 2 in its name, enter `2` into the search input, and you would find samples with names like:

* Sample_2
* Sample_293
* 02-2222

### Advanced Filtering

![Samples advanced filters.](images/advanced-filter.png)

Clicking the filter button <span class="fa fa-filter fa-fw"></span> opens a dialog where you can filter by sample name and / or  date modified.

![Samples advanced filter dialogue.](images/advanced-filter-dialogue.png)

Filtering by sample name will match the same as the search field, so the filter name will match *anywhere* in the sample name.

![Samples advanced filter dialogue daterange.](images/advanced-filter-dialogue-daterange.png)

To search sample by a date range, click on the date range field.  I drop down will be displayed with pre-determine ranges:

* Last 30 Days
* Last 60 Days
* Last 120 Days

Or you can enter a custom date range by selecting the dates in the calendar.

![Samples advanced filter dialogue apply.](images/advanced-filter-dialogue-apply.png)

To apply the selected filters click the 'Filter' button.

![Samples advanced filter applied state.](images/advanced-filter-applied-state.png)

Once the filter is applied, the samples table will be updated with the filtered samples.  When an advanced filter is applied, a tag is created below the filter button to allow the user to know what filters are currently applied.  To remove a specific filter click on the tag itself.

### Clearing Filters

![Samples clear filters button.](images/advanced-filter-clear.png)

To clear all currently applied filters and search, click on the clear button to the right of the filter area.

Filtering and Selecting by File
-------------------------------

As projects become larger, it becomes unwieldy to select a large subset of samples.  To facilitate this, there is the 'Filter by File' option.

* Create a `.txt` file that contains the name of each sample you want to select on a new line.  You can either:
    - Use a text editor like Windows Notepad or TextEdit on Mac (note: when creating a new text document in TextEdit, press cmd + shift + t to change to the `.txt` format)
    - Or create a spreadsheet in Excel with a single column and save the file as a 'Text (Tab Delimited) (*.txt)' file.

Example (`project_5_filter.txt`):

<pre>
03-3333
10-6966
15-7569
</pre>

* On the project samples page, click the <span class="fa fa-file-o fa-fw"></span> button from the filter menu.

![Filter by File Button](images/filter_by_file_btn.png)

* Select the file you created.  This will clear all previous filters, then filter and select the samples that have matching names to those in your list.

![Filter by File Button](images/filter_by_file_selected.png)


Viewing associated samples
--------------------------

You can quickly create an aggregated view of all of the samples in this project with all of the samples from both local and remote associated projects. To view associated samples, click the "Display" button and select which samples you would also like to see in the view by clicking on "Associated Project Samples" or "Remote Project Samples". Project managers may choose which samples will appear here by [adding or removing associated projects](../../user/project/#associated-projects).

![Sample type selector](images/display-sample-type.png)

Select which sources should be displayed in the table.

* <img src="images/local-color.png" class="inline"/> (Project Name) Samples - Samples belonging to the project.  Displayed in dark blue.
* <img src="images/associated-color.png" class="inline"/> Associated Project Samples - Samples from associated projects on the local IRIDA installation.  Displayed in green.
* <img src="images/remote-color.png" class="inline"/> Remote Project Samples - Samples from associated projects on remote IRIDA installations.  Displayed in gold.

Associated samples will be displayed in the project samples table designated with the same colours.

![Sample table with associated and remote samples](images/associated-display.png)

If a remote API connection is required, a warning box will be displayed to help connect you to the required API.

![Warning to connect to API](images/remote-warning.png)

Modifying samples
-----------------

Only user accounts that have the <img src="images/manager-icon.png" class="inline" alt="Manager role icon."> **Manager** role on a project can modify the samples in a project.

### Selecting samples

All sample modification actions require that samples be selected. You can select individual samples by clicking on the checkbox beside the sample:

![Selected sample.](images/selected-sample.png)

You may also select many samples using the "Select" drop-down that appears above the samples list on the right-hand side:

![Select button.](images/select-button.png)

The select button will always indicate the total number of selected samples in the project as a numeral with a gray background.

When you click on the select button, you have the option to select samples by:

* Page (selects all of the samples visible on your screen)
* All (selects **all** samples in the project)
* None (delesects all samples)

### Copying samples between projects

{% include tutorials/common/samples/copy-samples.md %}

### Moving samples between projects

An alternative to [copying samples between projects](#copying-samples-between-projects) is to **move** a sample between projects. Unlike copying, when a sample is moved, the original sample is removed.

Like copying samples, you must be a project <img src="images/manager-icon.png" class="inline" alt="Manager role icon."> **Manager** on **both** the project that you are moving the sample *from*, and the project that you are moving the sample *to*.

Start by [selecting the samples](#selecting-samples) that you want to move to the other project. When you've selected the samples that you want to move, click on the "Samples" button just above the samples list and select "Move Samples":

![Move samples button.](images/move-samples-button.png)

In the dialog that appears you will be presented with a list of the samples that are going to be moved, and an option to choose the project that the samples should be moved to:

![Move samples dialog.](images/move-samples-dialog.png)

When you click on the drop-down box to select a project, you can either visually find the project that you want, or you can filter the projects by their name by typing into the text field.

Once you've selected the project that you want to move the samples to, click on the "Move Samples" button.

### Merging samples within a project

If a sample was created when sequencing data was uploaded with an incorrect name, you may want to merge two samples together. When you merge two samples, you will move all of the **sequencing files** from one sample to another, then **delete the original sample**. **None** of the sample metadata will be copied between the merged samples, instead you will select one sample as the target for the sample merge. Only users with the project <img src="images/manager-icon.png" class="inline" alt="Manager role icon."> **Manager** role can merge samples in a project.

Start by [selecting the samples](#selecting-samples) that you want to merge. You **must** select more than one sample to enable the merge samples button. Once you've selected the two or more samples that you would like to merge, click on the "Samples" button just above the samples list and select "Merge Samples":

![Merge samples button.](images/merge-samples-button.png)

In the dialog that appears you will be presented with a list of the samples that are going to be merged, and an option to choose the target sample of the merge:

![Merge samples dialog.](images/merge-samples-dialog.png)

Click on the sample name under "**Select a sample to merge into**" to choose which sample will be used as the target for all of the sequencing data.

You may also (optionally) rename the target sample by entering a new sample name under "**Rename sample**". The sample name must be **at least** 3 characters long, and **must not** contain white space characters (tab or space) or any of the following characters: `? ( ) [ ] / \ = + < > : ; " , * ^ | & ' .`. If you do not want to rename the target sample, leave this field blank.

Once you've finished choosing the sample to merge into, click on the "Complete Merge" button at the bottom of the dialog.

Exporting samples
-----------------

The [pipelines](../pipelines) available in IRIDA may not be enough for the types of analysis that you want to run on your sequencing data. You can export your sample data from IRIDA in a number of different ways:

1. [Downloading samples](#downloading-samples),
2. [To the command-line](#command-line-export), or
3. [Directly to Galaxy](#galaxy-export)
4. [Upload to NCBI](#ncbi-upload)

All export options require that you [select the samples for export](#selecting-samples) before you are able to export the samples.

<blockquote>
<b>Tip</b>: For all types of export, you can export <b>all</b> of the data in a project using the <b>Select All</b> feature.
</blockquote>

#### Downloading samples

You can download an individual sequence file from a sample by [navigating to the file](#viewing-sequence-files), then clicking on the <span class="fa fa-fw fa-download"></span> icon (see: [Downloading a sequence file](#downloading-a-sequence-file)).

{% include tutorials/common/samples/download-samples.md %}

#### Command-line export

{% include tutorials/common/samples/command-line.md %}

#### Galaxy export

{% include tutorials/common/samples/galaxy-export.md %}

#### NCBI Upload

{% include tutorials/common/ncbi-export.md %}

<a href="../project/">Previous: Managing Projects</a><a href="../pipelines/" style="float: right;">Next: Launching Pipelines</a>
