/* global angular */
var irida = angular.module('irida', ['ngResource']);
irida.controller(ProjectsListCtrl);

var ProjectsListCtrl = function ($scope) {
  "use strict";
  $scope.projects = [];
};