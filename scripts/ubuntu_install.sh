#!/bin/bash

# Make a directory to put the executatble into
mkdir -p ~/.irida-npm;
pushd ~/.irida-npm > /dev/null

# Set up NodeJS
if [ ! -e node-v0.10.28-linux-x64 ] ; then
    echo "Installing nodejs(tailf ~/.irida-npm/nodejs.log)"
    # Download NodeJS
    wget -nc 'http://nodejs.org/dist/v0.10.26/node-v0.10.26-linux-x64.tar.gz'
    tar -zxf node-v0.10.26-linux-x64.tar.gz &> ~/.irida-npm/nodejs.log
    echo 'export PATH=~/.irida-npm/node-v0.10.26-linux-x64/bin:$PATH' > ~/.irida-npm/bashrc
    source ~/.irida-npm/bashrc;
fi

# Install phantomjs for testing
if [ ! -e bin/phantomjs ] ; then
    echo "Installing phantomjs(tailf ~/.irida-npm/phantomsjs.log)"
    mkdir -p bin
    pushd bin > /dev/null
    # Downlod phantomsjs
    wget 'https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-1.9.7-linux-x86_64.tar.bz2'
    tar xvfj phantomjs-1.9.7-linux-x86_64.tar.bz2 &> ~/.irida-npm/phantomjs.log
    mv phantomjs-1.9.7-linux-x86_64/bin/phantomjs ~/.irida-npm/bin/
    rm -r phantomjs-1.9.7-linux-x86_64*
    popd > /dev/null
    echo 'export PATH=~/.irida-npm/bin:$PATH' > ~/.irida-npm/bashrc
    source ~/.irida-npm/bashrc;
fi

# Install chromedriver for testing
if [ ! -e bin/chromedriver ] ; then
    echo "Installing chromedriver (tailf ~/.irida-npm/chromedriver.log)"
    mkdir -p bin
    pushd bin > /dev/null
    # Downlod chromedriver
    wget 'http://chromedriver.storage.googleapis.com/2.10/chromedriver_linux64.zip'
    unzip chromedriver_linux64.zip
    mv chromedriver ~/.irida-npm/bin/
    rm -r chromedriver_linux64.zip
    popd > /dev/null
    echo 'export PATH=~/.irida-npm/bin:$PATH' > ~/.irida-npm/bashrc
    source ~/.irida-npm/bashrc;
fi

popd > /dev/null

# Install Sass and Compass
echo "Installing ruby-sass and ruby-compass (tailf ~/.irida-npm/apt.log)"
sudo apt-get install -y ruby-sass ruby-compass &> ~/.irida-npm/apt.log

# Install Grunt CLI, Protractor, Karma and Bower
echo "Installing global npm packages. (tailf ~/.irida-npm/npm-global.log)"
npm install -g grunt-cli protractor bower &> ~/.irida-npm/npm-global.log

# Install local dependencies
echo "Installing local npm packages. (tailf ~/.irida-npm/npm-local.log)"
npm install &> ~/.irida-npm/npm-local.log;

echo "Installing bower packages. (tailf ~/.irida-npm/bower.log)"
bower install &> ~/.irida-npm/bower.log;

echo "Finished. Please add 'source ~/.irida-npm/bashrc' to your ~/.bashrc file."
