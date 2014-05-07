#!/bin/bash

# Download NodeJS
wget http://nodejs.org/dist/v0.10.28/node-v0.10.28-linux-x64.tar.gz

# Make a directory to put the executatble into
mkdir ~/Applications/

# Set up NodeJS
tar -zxf node-v0.10.28-linux-x64.tar.gz
cd node-v0.10.28-linux-x64/
./configure --prefix=~/Applications && make && make install;

# Add NodeJS to the path
echo 'export PATH=~/Applications/bin:${PATH}' >> ~/.bashrc;
source ~/.bashrc;

# Install Sass and Compass
sudo apt-get ruby-sass ruby-compass;

# Install Grunt CLI, Protractor, Karma and Bower
sudo npm install -g grunt-cli protractor bower;

# Install local dependencies
npm install;
bower install;

# Update webdriver
sudo webdriver-manager update;
