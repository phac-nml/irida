#!/bin/bash

# Download NodeJS
wget http://nodejs.org/dist/v0.10.28/node-v0.10.28-linux-x64.tar.gz

# Make a directory to put the executatble into
mkdir -p ~/.irida-npm;

# Set up NodeJS
tar -zxf node-v0.10.28-linux-x64.tar.gz;
mv node-v0.10.28-linux-x64/ ~/.irida-npm/
echo 'export PATH=$PATH:~/.irida-npm/node-v0.10.28-linux-x64/bin' >> ~/.bashrc;
source ~/.bashrc;
rm -r node-v0.10.28-linux-x64.tar.gz;

# Install Sass and Compass
sudo apt-get install ruby-sass ruby-compass;

# Install Grunt CLI, Protractor, Karma and Bower
npm install -g grunt-cli protractor bower;

# Install local dependencies
npm install;
bower install;

# Update webdriver
webdriver-manager update;