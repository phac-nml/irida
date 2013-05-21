/*global angular */
var NGS = angular.module('NGS', [
  'ui',
  'ngCookies',
  'ngResource',
  'NGS.Services'
]);

NGS.config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
  'use strict';
  $locationProvider.html5Mode(true);

  // Handle all routes
  $routeProvider
    .when('/login',
    {
      action: 'login.main'
    });
}]);

