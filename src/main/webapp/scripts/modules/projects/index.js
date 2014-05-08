'use strict';

var projectsCtrl = require('./ProjectsMainCtrl');

module.exports = angular.module('irida.projects', [])
    .controller('ProjectsMainCtrl', projectsCtrl);