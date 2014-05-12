OAuth2 Demo Web Client
======================
This package is a demo OAuth2 web client to be used with an IRIDA API package.

Requirements
------------
* Maven.
* An installation of the IRIDA API version 1.5 or higher that supports OAuth2 authorization_code clients.

Running
-------
This application is made to be run with Jetty on port 8181.  To launch the web server:

mvn jetty:run -Djetty.port=8181
