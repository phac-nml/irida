#!/bin/bash

sudo apt-get install nodejs-legacy npm ruby-sass ruby-compass;
sudo npm install -g grunt-cli protractor bower;
sudo chown -R $(whoami) ~/.npm
npm install;
bower install;
sudo webdriver-manager update;
