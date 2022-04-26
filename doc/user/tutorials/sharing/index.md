---
layout: default
title: "Sharing data within IRIDA"
search_title: "Sharing data within IRIDA"
description: "A tutorial on how to share your sequencing data that's stored in IRIDA with other IRIDA users."
---

Sharing data in IRIDA
=====================
{:.no_toc}

Once you've got data into IRIDA, either by [web upload][web-upload] or from a sequencing facility with the [IRIDA Uploader](https://github.com/phac-nml/irida-uploader), you're probably going to want to share that data with collaborators.

In order to share data from a project, you **must** have the **Manager** role on the project. The **Manager** role implies ownership of the data contained in the project. You can find out if you have the **Manager** role by looking at the [project members section][project-members].

You can share data with collaborators in IRIDA in two ways: to an individual user account, or to a group of user accounts.

If the individual person you want to share with within IRIDA does not have a user account, you must also have permission to create a user account for that user. If you do not, then you'll need to contact someone who has permissions to create user accounts.

* TOC
{:toc}

Creating a User Account
-----------------------

If the person you would like to share data with within IRIDA does not have a user account, you'll need to make an account for them.

You may create a new user account in IRIDA if you have the **Manager** or [Administrator](../../administrator/#creating-a-new-user-account) system role.

After logging in to IRIDA, click the gear icon on the top right-hand side of the navbar and click **Create User**:

![Create user menu.]({{ site.baseurl }}/images/tutorials/common/users/create-user-menu.png)

{% include tutorials/common/creating-a-user-account.md %}

Creating a User Group
---------------------

You may share data with groups of users in IRIDA by creating a user group.

{% include tutorials/common/creating-a-user-group.md %}
{% include tutorials/common/adding-a-member-to-a-group.md %}

Adding a Project Member
-----------------------

Once the user account or group is created, go to the project containing the data you want to share with this user, and select the **Members** tab.

![Project details members tab.]({{ site.baseurl }}/images/tutorials/common/projects/members-overview.png)

### Adding an Individual Member

{% include tutorials/common/project-add-member.md %}

### Adding a Group Member

{% include tutorials/common/project-add-member-group.md %}

Downloading Sample Data
-----------------------

All project members are allowed to export the samples in a project to the command line (add a link to the command line tutorial), to Galaxy (add a link to the Galaxy tutorial) or to download sample data. External collaborators will most likely want to download sample data to their own computer so that they can manage the data locally. External collaborators can use IRIDA to download [multiple samples in a zip package](#downloading-multiple-samples) or they can [download individual files](#downloading-individual-files) by examining each sample.

### Downloading Multiple Samples

{% include tutorials/common/samples/download-samples.md %}

### Downloading Individual Files

{% include tutorials/common/samples/view-individual-sample.md %}

{% include tutorials/common/samples/view-sequence-files.md %}

{% include tutorials/common/samples/download-sequence-file.md %}

[web-upload]: ../web-upload/
[project]: {{ site.baseurl }}/user/user/project/
[samples]: {{ site.baseurl }}/user/user/samples/
[project-members]: {{ site.baseurl }}/user/user/project/index.html#project-members
