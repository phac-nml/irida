---
layout: default
title: "Dashboard Overview"
search_title: "IRIDA Dashboard Overview"
description: "An overview of the features present on the IRIDA dashboard."
---

IRIDA Dashboard Overview
========================
{:.no_toc}

The IRIDA dashboard is the main location for accessing all of the data management and pipeline execution functionality built into IRIDA. This document describes the general features of the IRIDA dashboard.

* this comment becomes the TOC
{:toc}

Main Dashboard
--------------

The main IRIDA dashboard has several different areas:

* The [main menu](#main-menu), for accessing and managing data and pipelines,
* The [cart](#cart), for data selections,
* The [user settings](#user-settings) menu,
* The [logout](#logout) button,
* The [user project statistics section](#user-project-statistics) section,
* The [recent activities](#recent-activities) section, and
* The [updates](#updates-announcements) section.

![The main IRIDA dashboard.](images/dashboard.png)

Main Menu
---------

The main menu area is at the top, left-hand side of the IRIDA dashboard:

**Manager:**

![The manager main menu in the dashboard.](images/main-menu-user.png)

**User:**

![The user main menu in the dashboard.](images/main-menu-manager.png)


### Projects

From the main menu, you can click on the "Projects" button to access all projects that you are permitted to view or modify. You may also create a new project by clicking on "Projects", then "Create New Project".

For more information about managing your projects, please see the [managing projects](../project) section.

### Analyses

The analyses menu displays information about analysis pipelines available in IRIDA and links to how to monitory your running pipelines.

#### Pipelines
{:.no_toc}

You can view the pipelines that are installed in IRIDA by clicking on the "Pipelines" button.

For more information about running pipelines, please see the [launching pipelines](../pipelines) section.

#### Your Analyses
{:.no_toc}

After you've launched a pipeline, you can monitor its progress and view results by clicking on the "Analyses" button.

For more information about viewing pipeline results and working with analysis, please see the [viewing pipeline results](../pipelines/#viewing-pipeline-results) section.

Cart
----

The cart is a temporary area to keep a collection of samples that you intend to submit for use in a pipeline execution. The dashboard provides access to your cart on the top, right-hand corner of the page:

![The cart button in the dashboard.](images/cart.png)

For more information about using the cart, please see the [launching pipelines](../pipelines) section, and the [managing samples](../samples) section.

Settings
--------

The <i class="fa fa-cog"></i> menu allows you to manage a number of settings for IRIDA.

**Manager**

![Settings panel manager](images/settings-panel-manager.png)

**User**

![Settings panel user](images/settings-panel-user.png)

### Users (Manager)

From the main menu, you can click on the "Users" link and select "Users" from the dropdown to view the users available on the system.  You may add a new user by clicking on "Add User" button.


### Groups (Manager)

From the main menu, you can click on the "Users" link and select "User Groups" from the dropdown to view the groups available on the system.  You may create a new user group by clicking on "Create New User Group" button.

For more information about managing user groups, please see the [managing user groups](../user-groups) section.

### Remote instances of IRIDA

Remote instances of IRIDA can be used as data sources for associated projects and workflows.  The "Remote IRIDA Connections" section allows users to connect to remote instances of IRIDA.  

**Note**: You must have been provided with a username and password for a remote instance of IRIDA before you can use the data located on that instance.

Administrators can add or remove available remote instances of IRIDA from this menu.  Details can be found in the [administrator guide](../../administrator/#managing-remote-irida-connections).

#### Connecting to remote instances of IRIDA
{:.no_toc}

The Remote Connections list will show you all configured remote instances of IRIDA.  To connect, click the "Connect" button next to the right of the connection status.

![Remote instances list](images/api-list.png)

If you have never connected to the remote IRIDA instance before, or your login has expired, you will be shown a login window for the remote site.  Enter your login credentials for that site.  The username and password that you use to connect to a remote instance of IRIDA are different than the username and password that you used to log in to the local site.

![Remote instance login page](images/remote-login.png)

You will be shown an authorization page.  Read the details and click "Authorize" to connect to the API.

![OAuth2 approval](images/oauth-approval.png)

You will be shown a confirmation and your browser will reload the current page.  You should now be connected to the remote installation.

User Settings
-------------

The settings menu allows you to view and edit the details of your own user account. This menu also allows you to update your email subscription preferences.  You can find the settings menu in the top, right-hand corner of the page under the user icon:

![The settings menu in the dashboard.](images/user-settings.png)

For more information about the user settings page, see the [user settings page](user.html).

### Logout

Once you've finished working with IRIDA, we strongly recommend that you log out, especially if you are using a shared computer.

You can log out of IRIDA by clicking on the user settings menu in the top, right-hand corner of the page, and *Logout*.

User Project Statistics
------------------

The user project statistics appear at the top of the page.

![User project statistics section.](images/user-project-statistics.png)

The statistics section lists:
* The number of total projects the user is on (includes direct and indirect through a user group)
* The number of total samples in the projects
* The number of total analyses ran by the user

Recent Activities
-----------------

The recent activities panel appears on the left panel of the dashboard:

![Recent activities section.](images/recent-activities.png)

The recent activities section will show you things like:

* Users being added or removed from project access,
* Samples being added to projects.

All activities in the recent activities section will link to the project or user account that has been modified by the activity.

Updates-Announcements
---------------------

The IRIDA platform uses announcements to inform users of important information. Some examples may include:

* When scheduled downtime will happen
* Recently added features or bug fixes

There are priority and non-priority announcements, represented by the flag icon. A blue flag represents a priority announcement and a grey flag represents a non-priority announcement.
Priority announcements will be automatically displayed at login. These announcements must be acknowledged that they have been read before continuing.

To dismiss an announcement or mark it as read, hit the `Close` button. Or if more than one priority announcement exists, hit the `Next` button.

![Priority Announcement](images/announcements-priority-modal.png)

The total unread announcement count is represented by the badge number on the bell icon within the main navigation bar.

![Announcement Bell Icon](images/announcements-bell-icon.png)

To view the list of all unread announcements, hover over the bell icon for a drop down menu to appear.

![Announcement Submenu](images/announcements-submenu.png)

To view the announcement details, click on the announcement title link. A modal will open and display the announcement details. 
Hit the `Close`, `Next`, or `Previous` button to mark the announcement as read. To close the non-priority announcement and come back to it later, hit the `X` icon in the top-right corner of the modal. 

![Announcement Submenu](images/announcements-modal.png)

To view all announcements, hit the `View All` link from the drop down menu. This will bring you to the announcements page.

![Announcements Page](images/announcements-page.png)

To view the announcement details, click on the announcement title link.

<a href="../login/">Previous: Logging in</a><a href="../user-groups/" style="float: right;">Next: Managing user groups</a>
