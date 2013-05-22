/*global angular */
var NGS = angular.module('NGS', [
  'restangular',
  'ui',
  'ngCookies',
  'ngResource',
  'NGS.Services'
]);

NGS.config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
  'use strict';
  $locationProvider.html5Mode(true);
}]);

