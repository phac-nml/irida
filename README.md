IRIDA UI
========

Install Development Environment
-------------------------------

### Install Dependencies

1. Install [NodeJS](http://www.nodejs.org)
1. Install `grunt-cli`: `sudo npm -g install grunt-cli`
1. Install `bower`: `sudo npm -g install bower`
1. Install `sass` and `compass`: `sudo gem update; sudo gem install sass; sudo gem install compass;`

### Install Packages

1. Install node packages: `sudo npm install`
1. Install bower packages: `bower install`

### Install Live Reload Browser Extensions

This will allow you to make changes to the front end and have them reloaded automagically in the browser when you save a file.

﻿[Livereload Browser Extenions](http://feedback.livereload.com/knowledgebase/articles/86242-how-do-i-install-and-use-the-browser-extensions-)

Development
-----------

1. Start jetty
1. Start the `grunt` development environment: `grunt dev`
    1. Compiles the scss --> css
    1. Creates a watch (watches for changes to js, html, and scss files) and completes the appropriate actions.
    1. Starts a live reload server:
        1. Proxies requests from localhost:9000 --> localhost:8080
        1. Reloads localhost:9000 when changes occurs to the watched files