Creating a new User Group
-------------------------

{% include tutorials/common/creating-a-user-group.md %}

Viewing user group details
--------------------------

To view user group details, start from the [user groups list](#viewing-existing-user-groups) and click on the **Name** of the user group:

![User group name link.]({{ site.baseurl }}/images/tutorials/common/users/user-group-details-link.png)

The user group details page shows a summary of the user group details and provides a view of the user accounts that have membership in the group:

![User group details page.]({{ site.baseurl }}/images/tutorials/common/users/user-group-details.png)

Editing user group details
--------------------------

If you are a group owner, you can edit the details of a user group. To edit user group details, start by [viewing the user group details](#viewing-user-group-details), then click on the pencil icon beside the field you wish to edit:

![Edit group details button.]({{ site.baseurl }}/images/tutorials/common/users/edit-user-group-details.png)

When you've finished editing the user group field, you may click out of the input box or press enter, and it will save right away.

User group members
------------------

All users in IRIDA can see all user groups. A user can have one of two different roles in a user group: a group member (used only for project membership permissions) and a group owner (used for project membership permissions *and* group editing permissions).

### Adding a project member

If you are a group **owner**, you can add new members to the group.

{% include tutorials/common/adding-a-member-to-a-group.md %}

### Changing a user group member role

You may want to change a user group member role if you wish to remove permissions for an individual user account to modify user group details, but still want to allow that user account to view the project data that the user group is assigned to. You can only change a user group member role if you have the group **owner** role on the user group.

Start by [viewing the user group details](#viewing-user-group-details).

To change the role of a project member, click on the drop-down menu in the "Role" column on the table for the user you would like to change:

![User group role drop-down menu.]({{ site.baseurl }}/images/tutorials/common/users/user-group-details-role.png)

The user group role is saved as soon as you make a selection -- you **do not** need to click a "Save" button.

### Removing a user from a user group

You may want to completely remove all permissions for a user to access data in any project that the group is assigned to. To remove those permissions, you must remove the user account from the user group.

Start by [viewing the user group details](#viewing-user-group-details).

To remove a user group member, click on the "X" button on the right-hand side of the table:

![Remove user group member button.]({{ site.baseurl }}/images/tutorials/common/users/user-group-details-remove-user.png)

When you click the remove button, you will be asked to confirm the user group member removal:

![Remove user group member confirmation dialog.]({{ site.baseurl }}/images/tutorials/common/users/user-group-details-remove-user-modal.png)

To confirm, click the "OK" button.