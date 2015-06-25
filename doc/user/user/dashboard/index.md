---
layout: default
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
* The [settings](#settings) menu,
* The [logout](#logout) button, and
* The [recent activities](#recent-activities) section.

![The main IRIDA dashboard.](images/dashboard.png)

Main Menu
---------

The main menu area is at the top, left-hand side of the IRIDA dashboard:

![The main menu in the dashboard.](images/main-menu.png)

### Projects

From the main menu, you can click on the "Projects" button to access all projects that you are permitted to view or modify. You may also create a new project by clicking on "Projects", then "Create New Project".

For more information about managing your projects, please see the [managing projects](../project) section.

### Pipelines

You can view the pipelines that are installed in IRIDA by clicking on the "Pipelines" button.

For more information about running pipelines, please see the [launching pipelines](../pipelines) section.

### Analyses

After you've launched a pipeline, you can monitor its progress and view results by clicking on the "Analyses" button.

For more information about viewing pipeline results and working with analysis, please see the [viewing pipeline results](../pipelines/#viewing-pipeline-results) section.

Cart
----

The cart is a temporary area to keep a collection of samples that you intend to submit for use in a pipeline execution. The dashboard provides a quick summary of the contents of your cart on the top, right-hand corner of the page:

![The cart button in the dashboard.](images/cart.png)

You can click on the cart button to reveal more information about the contents of your cart:

![Viewing the contents of the cart.](images/cart-contents.png)

For more information about using the cart, please see the [launching pipelines](../pipelines) section, and the [managing samples](../samples) section.

Remote APIs
-----------
Remote IRIDA installations can be used as data sources for associated projects and worklows.  The "Remote APIs" section allows users to connect to remote IRIDA installations.  

**Note**: You must have been provided with a username and password for a remote instance of IRIDA before you can use the data located on that instance.

![Remote API button](images/remote-api-dash.png)

Adminstrators can add or remove available Remote APIs from this menu.  Details can be found in the [administrator guide](../../administrator/#managing-remote-apis).

### Connecting to Remote APIs

The Remote APIs list will show you all configured Remote APIs.  To connect, click the "Connect" button next to an API.

![Remote API list](images/api-list.png)

If you have never connected to the remote API before, or your login has expired, you will be shown a login window for the remote site.  Enter your login credentials for that site.  The username and password that you use to connect to a remote instance of IRIDA are different than the username and password that you used to log in to the local site.

![Remote API login page](images/remote-login.png)

You will be shown an authorization page.  Read the details and click "Authorize" to connect to the API.

![OAuth2 approval](images/oauth-approval.png)

You will be shown a confirmation and your browser will reload the current page.  You should now be connected to the remote installation.

Settings
--------

The settings menu allows you to view and edit the details of your own user account. You can find the settings menu in the top, right-hand corner of the page:

![The settings menu in the dashboard.](images/settings.png)

User accounts with the administrator role can also use the settings menu to add and remove software client details from IRIDA. For more information about managing clients in IRIDA, please see the [managing system clients](../../administrator/#managing-system-clients) section in the [administrator guide](../../administrator).

Logout
------

Once you've finished working with IRIDA, we strongly recommend that you log out, especially if you are using a shared computer.

You can log out of IRIDA by clicking on the <img src="images/logout-icon.png" alt="logout icon" class="inline"> in the top, right-hand corner of the page:

![The logout button in the dashboard.](images/logout.png)

Recent Activities
-----------------

The recent activities panel appears in the middle of the dashboard:

![Recent activities section.](images/recent-activities.png)

The recent activities section will show you things like:

* Users being added or removed from project access,
* Samples being added to projects.

All activities in the recent activities section will link to the project or user account that has been modified by the activity.
