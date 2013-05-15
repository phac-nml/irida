/*global angular */
angular.module('irida', [
    'ui',
    'irida.services'
  ])
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';
    $locationProvider.html5Mode(true);
    $routeProvider.otherwise({redirectTo: '/'});
  }]);

