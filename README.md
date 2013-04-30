IRIDA Web
---------
`irida-web` is the web-interface for IRIDA.

The IRIDA API project requires the creation of certain directories prior to execution:

    sudo mkdir -p /home/irida/sequence-files
    sudo chown -R you:bioinformatics /home/irida/

Client Side Testing Set-up
==========================
1. Download and install [node.js](http://nodejs.org)
2. In irida-web main directory:
    1. `npm install -g karma@~0.9.2`
    2. `npm install -g phantomjs@~1.8`
    3. `npm install`

Package Client Side
===================

1. `mvn package`
