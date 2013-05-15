/*global angular */
angular.module('irida', [
    'ui',
    'irida.services'
  ])
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';
    $locationProvider.html5Mode(true);
    $routeProvider.otherwise({redirectTo: '/'});
  }])

/**
 * AppCtrl
 * Handles global variables and common functions
 */
  .controller('AppCtrl', ['$scope', 'authService', function ($scope) {
    'use strict';
    $scope.notifier = {
      message: '',
      icon: ''
    };
  }])

;

