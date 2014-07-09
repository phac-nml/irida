#!/bin/bash

# Make a directory to put the executatble into
mkdir -p ~/.irida-client;
pushd ~/.irida-client > /dev/null

# Set up NodeJS
if [ ! -e node-v0.10.28-linux-x64 ] ; then
    echo "Installing nodejs(tailf ~/.irida-client/nodejs.log)"
    # Download NodeJS
    wget -nc 'http://nodejs.org/dist/v0.10.26/node-v0.10.26-linux-x64.tar.gz'
    tar -zxf node-v0.10.26-linux-x64.tar.gz &> ~/.irida-client/nodejs.log
fi

# Install phantomjs for testing
if [ ! -e bin/phantomjs ] ; then
    echo "Installing phantomjs(tailf ~/.irida-client/phantomsjs.log)"
    mkdir -p bin
    pushd bin > /dev/null
    # Downlod phantomsjs
    wget 'https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.7-linux-x86_64.tar.bz2'
    tar xvfj phantomjs-1.9.7-linux-x86_64.tar.bz2 &> ~/.irida-client/phantomjs.log
    mv phantomjs-1.9.7-linux-x86_64/bin/phantomjs ~/.irida-client/bin/
    rm -r phantomjs-1.9.7-linux-x86_64*
    popd > /dev/null
fi

# Install chromedriver for testing
if [ ! -e bin/chromedriver ] ; then
    echo "Installing chromedriver (tailf ~/.irida-client/chromedriver.log)"
    mkdir -p bin
    pushd bin > /dev/null
    # Downlod chromedriver
    wget 'http://chromedriver.storage.googleapis.com/2.10/chromedriver_linux64.zip'
    unzip chromedriver_linux64.zip
    mv chromedriver ~/.irida-client/bin/
    rm -r chromedriver_linux64.zip
    popd > /dev/null
fi
echo 'export PATH=~/.irida-client/bin:~/.irida-client/node-v0.10.26-linux-x64/bin:$PATH' > ~/.irida-client/bashrc
source ~/.irida-client/bashrc
popd > /dev/null

# Install Sass and Compass
echo "Installing ruby-sass and ruby-compass (tailf ~/.irida-client/apt.log)"
sudo apt-get install -y ruby-sass ruby-compass &> ~/.irida-client/apt.log

# Install Grunt CLI, Protractor, Karma and Bower
echo "Installing global npm packages. (tailf ~/.irida-client/npm-global.log)"
npm install -g grunt-cli protractor bower &> ~/.irida-client/npm-global.log

# Install local dependencies
echo "Installing local npm packages. (tailf ~/.irida-client/npm-local.log)"
npm install &> ~/.irida-client/npm-local.log;

echo "Installing bower packages. (tailf ~/.irida-client/bower.log)"
bower install &> ~/.irida-client/bower.log;

echo "Finished. Please add 'source ~/.irida-client/bashrc' to your ~/.bashrc file."
