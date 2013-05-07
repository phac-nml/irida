/* global angular */
angular.module('irida.landing', [])

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when(
      '/', {
        templateUrl: './partials/landing.html',
        controller: 'LandingCtrl'
      })
  }])
    .controller('LandingCtrl', ['$scope', function ($scope) {
      'use strict';

    }]);
