/*global angular */
var NGS = angular.module('NGS', [
  'ui',
  'ngCookies',
  'ngResource',
  'infinite-scroll',
  'NGS.Services'
]);

NGS.config(['$locationProvider', function ($locationProvider) {
  'use strict';
  $locationProvider.html5Mode(true);
}]);

