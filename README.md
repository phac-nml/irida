IRIDA UI
========

Install Development Environment
-------------------------------

### Install Dependencies

#### On Ubuntu

1. Run: `./scripts/ubuntu_install.sh`
1. Add 'source ~/.irida-npm/bashrc' to your ~/.bashrc file.
1. You're done installing!

#### Other Operating Systems

1. Install [NodeJS](http://www.nodejs.org)
1. Install [PhantomJS](http://phantomjs.org/download.html)
1. Install `grunt-cli`: `sudo npm -g install grunt-cli protractor`
1. Install `sass` and `compass`: `sudo gem update; sudo gem install sass; sudo gem install compass;`
1. Install Global NPM dependencies: `sudo npm install -g grunt-cli bower`;
1. Install node packages: `sudo npm install`
1. Install bower packages: `bower install`

### Install Live Reload Browser Extensions

This will allow you to make changes to the front end and have them reloaded automagically in the browser when you save a file.

[Livereload Browser Extenions](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-)

Updating Development Environment
--------------------------------

### Install Dependencies (Ubuntu and Mac)

1. Run: `./scripts/updateDevDepenecies.sh`
2. You're done updating!

Development
-----------

1. Start jetty: `mvn jetty:run`
1. Run: `./scripts/devServer.sh`
    1. Compiles the scss --> css
    1. Creates a watch (watches for changes to js, html, and scss files) and completes the appropriate actions.
    1. Starts a live reload server:
        1. Proxies requests from localhost:9000 --> localhost:8080
        1. Reloads localhost:9000 when changes occurs to the watched files.
        1. Files will reload in the browser if you install Live Reload Browser Extension.

### Grunt

1. `grunt dev`: development environment including live reload.

Maven
-----

#### Verify

1. During verify: `mvn verify`

#### Package

`mvn package -Pprod`

### External Configuration

An external configuration file can be installed in /etc/irida/web.conf to set some web specific configuration parameters:

* mail.server.host -- The mail server to send emails from
* mail.server.protocol -- The mail protocol
* mail.server.email -- Reply-to email address
* server.base.url -- URL of the IRIDA instance for email links
* mail.server.username -- Reply-to name in the emails

