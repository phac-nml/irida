/*global angular */
angular.module('irida', [
    'ui',
    'ngResource',
    'irida.Services'
  ])
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';
    $locationProvider.html5Mode(true);
  }]);

