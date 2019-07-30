---
layout: default
search_title: "Administrator User Guide"
description: "A guide for demonstrating the administrative features of the IRIDA web interface."
---

Administrator User Guide
========================
{:.no_toc}

This guide shows how to use the administrative features of the IRIDA web interface.

* This comment becomes the table of contents
{:toc}

Managing User Accounts
======================

Only one account is created for a new instance of IRIDA by default: the administrator account.

This section of the guide describes how to create, modify, and remove local user accounts from IRIDA.

Creating a New User Account
---------------------------

{% include tutorials/common/creating-a-user-account.md %}

Updating an Existing User Account
---------------------------------

{% include_relative includes/start-from-the-user-list.md %}

To edit a user account, click on the "Edit" button at the top of the "User Details" table:

![User details edit button.](images/user-details-edit-button.png)

All of the fields that were available to modify when creating a user account are available for editing:

![Edit user details.](images/edit-user-details.png)

When you're finished updating the user account, you can click the "Update User" button at the bottom of the form to save the changes.

Resetting a Password
--------------------

{% include_relative includes/start-from-the-user-list.md %}

To reset the password for a user account, click on the "Reset Password" button at the top of the "User Details" table:

![User details reset password button.](images/user-details-reset-password-button.png)

When you click the button, you will be asked to confirm if you would like to reset the password for the user account:

![Reset password confirmation dialog.](images/reset-password-confirm.png)

You should see a success notification in the top, right hand side of your browser window when you confirm:

![Reset password success notification.](images/reset-password-success.png)

**Note**: Clicking the password reset button does *not* create a new password for the user, rather issues a reset request. The reset request will send an e-mail to the user with an account activation link (like when a user account is initially created), where the user will be allowed to create a new password.

Disabling a User Account
------------------------

{% include_relative includes/start-from-the-user-list.md %}

To edit a user account, click on the "Edit" button at the top of the "User Details" table:

![User details edit button.](images/user-details-edit-button.png)

To disable a user account, uncheck the "Enabled" field, and click "Update User":

![Uncheck the "Enabled" field.](images/uncheck-enabled.png)

**Note**: A user account that is not "Enabled" is **not** deleted from the system, but that user can no longer log in to IRIDA.

Managing System Clients
=======================

External tools can interact with IRIDA using the IRIDA REST API. External tools are required to use the OAuth2 framework for authorization. You must create an entry in the IRIDA database for each external tool that wants to interact with the IRIDA REST API. The tool author requires a client ID and a client secret. In addition to the client ID and secret, you are responsible for deciding what type of access a client should have (read, write, or both) and *how* the client should be allowed to interact with the REST API using user credentials.

Creating a New System Client
----------------------------

{% include tutorials/clients/creating-a-client.md %}

### Client ID
{:.no_toc}
The client ID must be unique in IRIDA, no two clients can share the same client ID. A client ID is required, and must be shorter than 255 characters, but no other validation is applied to client IDs.

### Token Validity
{:.no_toc}
When a client first interacts with the IRIDA REST API, it must provide its client ID, client secret, and user credentials. On successful authentication, the authorization server provides the client with an access token. The access token has a limited lifetime and expires after a certain period of time.

You should choose an access token lifetime similar to the amount of time that the client needs to interact with the REST API. For example, when a sequencer requires less than one hour of time to upload a sequencing run, so the lifetime of the access token should be around 1 hour.

### Grant Types
{:.no_toc}
You may also choose *how* a client is allowed to provide authentication details from a user. IRIDA currently supports two grant types:

1. Password, and
2. Authorization code.

You should choose the password grant type for applications that **are not** web-based applications, usually desktop or command-line clients. The password grant type requires the application to collect the user name and password directly from the user and submits the user name and password over HTTPS.

You should choose the authorization code grant type for applications that **are** web-based applications. In the authorization code grant type the user's web browser is redirected to IRIDA to allow the user to authorize the client's interaction with IRIDA. In the authorization code grant type, the user's username and password are *never* provided to the client.

### Refresh Tokens
{:.no_toc}
Refresh tokens allow client applications to generate a new authorization token without input from the user.  Because refresh tokens are valid for much longer than authorization tokens, refresh tokens can be used to ensure a connection can be made to an IRIDA API for longer running jobs.  Refresh tokens should be used for instances that a client application will require a long term connection to the IRIDA API where users may not be able to validate their connection.

You should choose a refresh token validity suitable for the client application.  Once a refresh token expires it will require a user to log in and reconnect to the IRIDA API.  It is suggested to use refresh tokens for clients which will be used for project synchronization. 

### Scopes
{:.no_toc}

Finally, you must decide what the client is allowed to do once it's been authorized to use the REST API. You must choose *at least one* scope, otherwise the client will not be allowed to do anything once it's been authorized to use the REST API.

If you choose *only* read scope, the client will have read-only access to the REST API (it will not be allowed to create new resources). The read scope is typically the only scope that should be enabled for a client.

If a client is going to be adding or modifying data in IRIDA, you should also enable the write scope. One specific example of a client that *must* be able to write to the REST API is the sequencer uploader tool.

Updating an Existing System Client
----------------------------------

{% include_relative includes/select-a-specific-client.md %}

To modify the client details, you can click on the "Edit" button, in the top, right-hand corner of the "Client Details" panel:

![Client details edit button.](images/client-details-edit-button.png)

From the client details page you can modify the client ID, the length of time that an authorization token is valid for, the grant type that the client uses, and the scopes (read or write) that the client is allowed to use:

![Edit client details panel.](images/edit-client-details.png)

After you have made the modifications that you want to the existing client, click on the "Update Client" button:

![Edit client details update button.](images/edit-client-details-update-button.png)

### Generating a New Client Secret

You may want to generate a new client secret for a client if, for example, the client secret has been accidentally leaked by the client and someone is abusing your API.

Client secrets are generated randomly by IRIDA. You can have IRIDA issue a new client secret by navigating to the ["Edit Client Details"](#updating-an-existing-system-client) page (as above), and checking "Generate a new client secret":

![Generate new client secret checkbox.](images/edit-client-details-generate-new-secret.png)

To generate the new secret, click on the "Update Client" button. The new client secret will be listed in the "Client Details" panel.

Removing a System Client
------------------------

{% include_relative includes/select-a-specific-client.md %}

To remove a client, click on the "Remove" button at the top, right-hand corner of the "Client Details" panel:

![Client details panel delete button.](images/client-details-delete-button.png)

You will be asked to confirm the deletion:

![Delete client confirmation dialog.](images/delete-client-confirm.png)

Click "OK" to confirm, or "Cancel" to decline the deletion.

After deleting a client, you will be returned to the list of clients.

Managing Remote IRIDA Connections
=================================
Remote instances of IRIDA can be used as data sources for associated projects and workflows.  Users will be able to view an aggregated view of samples from the local project along with all local and associated projects.  Administrators can create connections to remote instances of IRIDA using OAuth2 similar to how external tool developers can connect to the IRIDA REST API.

![Remote API connection](images/api-diagram.png)

See the [user guide](../../user/user/dashboard#managing-remote-irida-connections) for information on connecting to remote instances of IRIDA.


Allowing access to your IRIDA installation
------------------------------------------

A Remote Connection is granted with the same process as remote clients.  To allow a remote IRIDA instance to access your IRIDA instance, an OAuth2 client must be created with the following specifications:

* Token Validity: 12 hours recommended.  A short token validity will require users to reauthorize the API more often.
* Grant Type: `authorization_code`
* Scopes: `read`

**Note:** In order for a remote client to communicate with the REST API, the client must be able to access the REST API via HTTP.  This may involve opening access in a firewall or setting up a VPN connection between sites.

For more information about creating OAuth2 clients see the [managing system clients](#managing-system-clients) section.

Adding a Remote IRIDA installation
----------------------------------

Before a user can use a remote instance of IRIDA as a data source for associated projects and workflows, an administrator must create a connection to that remote instance (similar to how external tool developers can connect to the REST API).  To add a Remote Connection, an administrator will need the following information from the remote installation:

* An OAuth2 client ID which has been set up on the remote installation.
* An OAuth2 client secret.
* The root address of the IRIDA REST API of the remote installation.

After being given the client credentials, go to the "Remote IRIDA Connections" page.

![Remote API page](images/remote-api-dash.png)

The Remote Connections page will display all currently available Remote Connections and their connection status with an IRIDA instance.

To add a new connection, click the "Add Remote Connection" button:

![Add api](images/add-api.png)

Enter a suitable name for the remote installation.  This name will be visible to users connecting and accessing data from that instance.  Also enter the "Client ID", "Client Secret", and "Service URL" of the Remote Connection as given by the remote administrator.

![Create API](images/create-api-details.png)

After adding the Remote Connection you should be directed to the details page for that API.

To test the Remote Connection, click the "Connect" button.  You should be directed to a login page for the remote connection.  To connect to the remote instance of IRIDA you must have been given a username and password for the remote installation.  See [Remote instances of IRIDA](../user/dashboard/#remote-instances-of-irida) for more details.

![API Details](images/api-details.png)

Removing a Remote Connection
----------------------------
From the Remote Connections list page, click on the name of the connection you wish to delete.  This will bring you to the API details page.  Click the "Remove" button on that page and confirm.

![Remove API](images/remove-api.png)

Setting Analysis Submission Priority
====================================

Administrators have the ability to set the priority of analysis submissions in IRIDA.  This feature can be used to ensure important analyses are run in a timely fashion when the analysis queue is filled by routine or other low priority analyses.  By default all manually submitted submissions will have a priority of `MEDIUM`, whereas all automated submissions (such as automated assemblies and automated SISTR) will have `LOW` priority.

To update the priority of a submission, first view the submission page.  The current priority will be displayed in the info section

![Analysis priority.](images/analysis-priority.png)

To edit the priority click the pencil icon:

![Analysis edit button.](images/analysis-edit-button.png)

In this menu you can edit the priority of the analysis submission:

![Analysis priority edit.](images/analysis-priority-edit.png)

Note that a priority change will only effect analyses in the `Queued` state.  The analysis engine will pick higher priority analyses to begin running first, but currently running jobs will not be bumped by a higher priority analysis.  

Viewing Sequencing Runs
=======================
Start by clicking on the "Admin" menu (in the top, right-hand corner of the screen) and selecting "Sequencing Runs":

![Sequencing runs menu.](images/admin-sequencing-runs-menu.png)

The "Sequencing Runs" page lists all of the sequencing runs uploaded to IRIDA:

![Sequencing runs list.](images/sequencing-runs-list.png)

You can view the specific details about a sequencing run by clicking on the ID of the sequencing run:

![Select a specific run.](images/sequencing-runs-list-select.png)

The sequencing run details page shows information that was captured from the sequencing run file, and provides you a method to view a complete collection of files that were uploaded as part of a run:

![Sequencing run details page.](images/sequencing-run-details.png)

To delete a sequencing run, click the `Remove` button on the details page.  Note: This will remove the sequencing run and all assocaited sequence files.  If this operation results in a sample being empty, the sample will also be removed.

![Sequencing run remove.](images/remove-run.png)

To view the collection of files that were uploaded as part of the run, click on the "Files" tab, above the quick-details pane:

![Sequencing run details file tab.](images/sequencing-run-details-files-tab.png)

On the sequencing run files list you can download individual files by clicking on the ![download icon](images/file-download-icon.png) button, or you can view the quality control results produced by FastQC by clicking on the filename in the table:

![Sequencing run files list.](images/sequencing-run-files-list.png)

On the quality control report page you can view individual quality control metrics, like quality constrol charts and some statistics about the file:

![Sequencing run file quality control page.](images/sequencing-run-file-qc.png)

You can also view the overrepresented sequences detected by FastQC by clicking on the "Overrepresented Sequences" tab on the top, left-hand corner of the page:

![Sequencing run file overrepresented sequences tab.](images/sequencing-run-file-qc-overrepresented-sequences-tab.png)

The overrepresented sequences page displays all of the overrepresented sequences detected by FastQC:

![Sequencing run file overrepresented sequences page.](images/sequencing-run-overrepresented-sequences.png)

Uploading Data to IRIDA
=======================
IRIDA comes with a tool for uploading data from an Illumina MiSeq instrument to IRIDA using the REST API. Please see the [uploading data guide](uploader-tool/) for more information on how to upload data.

Displaying IRIDA Platform Updates/Announcements
===============================================
Announcements and updates can be displayed to users after they log in:

![Announcements panel display](images/announcements-display-to-user.png)

Administrators can create new announcements or view existing ones by going into the Admin menu and clicking `Announcements`:

![Announcements administrator menu option](images/announcements-display-to-user.png)

This will bring you into the Announcement Control Centre:

![Announcements control centre page](images/announcements-control-centre.png)

New Announcements can be created by clicking on the `Create New Announcement` button at the top-left. These announcements are rendered as markdown. Saving the new announcement will allow users to see this on the front page of IRIDA.

![Announcements create a new announcement page](images/announcements-create-new.png)

To edit an announcement, or to see who has read the announcement, you can click on the `View Details or Edit Announcement` button: 

![Announcements view details or edit announcement button](images/announcements-view-details-button.png)

The announcement can be edited and saved, or deleted from this page. You can also view a list of users who have marked this particular announcement as read, as well as see when they did so:

![Announcements edit or view details page](images/announcements-edit-details.png)


Viewing All Project Exports
===========================
Administrators have the ability to view the status of all exports to NCBI in the system.  Start by clicking on the "Admin" menu (in the top, right-hand corner of the screen) and selecting "NCBI Export Uploads":

![Sequencing runs menu.](images/admin-exports-menu.png)

You will be displayed all the NCBI exports that have been created sorted by the date they were created.

![Sequencing runs menu.](images/admin-exports-list.png)

The exports will have one of the following states:

 * `NEW`: Newly created submission that has not yet been uploaded
 * `UPLOADING`: Submission currently being uploaded
 * `UPLOADED`: Submission has been successfully uploaded
 * `UPLOAD_ERROR`: An error occurred while uploading
 * `created`: Submission created and being populated
 * `failed`: Failed immediately after submission
 * `queued`: Queued for processing by NCBI's processing pipeline
 * `processing`: Processing started by NCBI's processing pipeline
 * `processed-ok`: Processing completed successfully
 * `processed-error`: Processing completed with error(s)
 * `waiting`: Waiting for other files to continue
 * `submitted`: Submitted to NCBI
 * `Submission deleted`: Submission deleted
 * `retried`: Retried processing of failed action(s)
 
Troubleshooting
===============

To troubleshoot common problems in IRIDA, please see the **Troubleshooting** guides.

 1. [Troubleshooting Pipelines](troubleshooting/pipelines)