---
layout: default
title: "Sharing some data from a project within IRIDA"
search_title: "Sharing some data from a project within IRIDA"
description: "A tutorial on how to share some of your sequencing data that's stored in IRIDA with other IRIDA users."
---

Sharing some data from a project
================================
{:.no_toc}

Sometimes you only want to share a small subset of the data in one of your projects with another user. You can share out smaller sets of samples from a project by creating a new project in IRIDA, copying some samples to it, then adding new members to that project.

You must have the role of **Manager** on the project to copy samples to another project.

### Create a new project

{% include tutorials/common/creating-a-project.md %}

Once the new project is created return to the project that you would like to copy samples from.

### Copy Samples

{% include tutorials/common/samples/copy-samples.md %}

### Adding a Project Member

Once the samples are copied over to the new project, the last step is to add all users to this project that need access to this data.

![Project details members tab.]({{ site.baseurl }}/images/tutorials/common/projects/project-details-members-tab.png)

{% include tutorials/common/project-add-member.md %}



[web-upload]: ../web-upload/
[project]: {{ site.baseurl }}/user/user/project/
[samples]: {{ site.baseurl }}/user/user/samples/
[project-members]: {{ site.baseurl }}/user/user/project/index.html#project-members
