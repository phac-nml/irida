/* global angular */
angular.module('irida')

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    'use strict';
    $routeProvider.when(
      '/landing', {
        templateUrl: './partials/landing.html',
        controller: 'LandingCtrl'
      });
  }])
  .controller('LandingCtrl', [function () {
    'use strict';

  }]);
