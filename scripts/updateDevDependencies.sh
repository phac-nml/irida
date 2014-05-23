#!/bin/bash

# Update NPM dependencies
echo "Updating local npm packages (tailf ~/.irida-npm/npm-local.log)"
npm install &> ~/.irida-npm/npm-local.log;

echo "Updating bower packages. (tailf ~/.irida-npm/bower.log)"
bower install &> ~/.irida-npm/bower.log;

echo "Creating the current front-end (tailf ~/.irida-npm/frontend-init.log)"
grunt build &> ~/.irida-npm/frontend-init.log;
