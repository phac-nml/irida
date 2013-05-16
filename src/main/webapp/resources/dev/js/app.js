/*global angular */
angular.module('irida', [
    'ui',
    'restangular',
    'irida.Services'
  ])
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';
    $locationProvider.html5Mode(true);
  }]);

