OAuth2 Demo Web Client
======================
This package is a demo OAuth2 web client to be used with an IRIDA REST API.  This application demonstrates the process to get an OAuth2 token using the authorization_code grant type.

Running
-------
To launch the web server:

mvn jetty:run

The page can then be accessed at http://localhost:8181

Classes
-------

**HomeController**

Requests the user to enter the details for an OAuth2 protected REST API.

* Service URI - The base location of the REST API.
* Client ID - The client ID set in that api.
* Client ID - The client secret set in that api.

After the user enters this information, an authorization request will be created in *OltuAuthorizationController*.

**OltuAuthorizationController**

Uses the entered client information to request an authorization_code from the REST API, exchanges that authorization_code for an OAuth2 token, then forwards that token back to HomeController.


Get OAuth2 token using Authorization Code
-----------------------------------------

1. Request authorization code from /api/oauth/authorize.  Include following params:
1. * Redirect URI to your to receive authorization code location.
1. * ClientId of galaxy linker client
1. * ResponseType of "authorization_code"
1. * Scope of "read"

2. The client may be directed to login and to authorize the client access.

3. Authorization code will be sent to your authorization code location redirect URI as "code" param

4. Request token with authoriztion code from /api/oauth/token.  Include following params:
4. * ClientId of linker client
4. * ClientSecret of linker client
4. * The same redirect URI as in step 1
4. * Grant type of "authorization_code"
