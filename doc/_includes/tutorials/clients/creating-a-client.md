First navigate to the clients page via the admin panel side menu:

![Clients side menu link.](images/clients-side-menu.png)

The clients list shows all clients that are currently allowed to access the IRIDA REST API.

You can add a new client by clicking on the "Add Client" button:

![Add client button.](images/add-client-button.png)

When you add a client, you'll be required to provide the following information:

1. A unique client ID,
2. How long a token should be valid for once issued,
3. The type of OAuth2 flow that a client should use,
4. Whether to allow refresh tokens.
5. The scopes that the client is allowed to use (should the client be allowed to read, write, or both with the REST API).


![Client details.]({{ site.baseurl }}/images/tutorials/clients/client-details.png)
