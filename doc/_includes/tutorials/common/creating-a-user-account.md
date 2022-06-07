Fill in the user details fields. All fields are required for a user account in IRIDA:

![User details form.]({{ site.baseurl }}/images/tutorials/common/users/user-details-form.png)

By default, a user will be issued a randomly-generated, one-time-use key to activate their account. The first time the user logs in, they will be required to change their password. You may manually enter a user password by unchecking "Send Activation Email":

![Manual password entry.]({{ site.baseurl }}/images/tutorials/common/users/manual-user-password.png)

Passwords must meet the following requirements:

{% include tutorials/common/password-requirements.md %}

If you manually set a password for a new user account, the user will not be required to change their password on first log in.

When you click "Create User", an e-mail will be sent to the user (to the e-mail address you entered) including the URL for IRIDA (as configured in the [Administrator Install Guide]({{ site.baseurl }}/administrator/web/#web-configuration)). If you did not manually set a password for the user, the e-mail will include a link to activate the user account:

![User welcome e-mail.]({{ site.baseurl }}/images/tutorials/common/users/user-welcome-email.png)

On successfully creating a new user account, you will be redirected back to the admin users table and see a successful notification pop-up:

![User details page.]({{ site.baseurl }}/images/tutorials/common/users/create-user-success.png)

**Note**: Only announcements created within the last month will be shown to new users.