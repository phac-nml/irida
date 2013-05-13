/* global angular */
angular.module('irida')

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when(
      '/landing', {
        templateUrl: './partials/landing.html',
        controller: 'LandingCtrl'
      })
  }])
  .controller('LandingCtrl', ['$scope', function ($scope) {
    'use strict';

  }]);
