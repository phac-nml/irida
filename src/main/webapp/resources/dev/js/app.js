/*global angular */
angular.module('irida', [
    'ui',
    'ngCookies',
    'irida.Services'
  ])
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';
    $locationProvider.html5Mode(true);
  }]);

