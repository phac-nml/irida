Start by clicking on the "Admin" menu (in the top, right-hand corner of the screen) and selecting "Clients":

![Administrator clients menu.]({{ site.baseurl }}/images/tutorials/clients/admin-clients-menu.png)

The clients list shows all clients that are currently allowed to access the IRIDA REST API.

You can add a new client by clicking on the "Add Client" button:

![Add client button.]({{ site.baseurl }}/images/tutorials/clients/clients-list-add-client-button.png)

When you add a client, you'll be required to provide the following information:

1. A unique client ID,
2. How long a token should be valid for once issued,
3. The type of OAuth2 flow that a client should use,
4. Whether to allow refresh tokens.
5. The scopes that the client is allowed to use (should the client be allowed to read, write, or both with the REST API).


![Client details.]({{ site.baseurl }}/images/tutorials/clients/client-details.png)
