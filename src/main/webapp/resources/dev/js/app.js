/*global angular */
angular.module('irida', [
    'http-auth-interceptor',
    'irida.users',
    'ajaxService',
    'CookieService'
  ])
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';
    $locationProvider.hashPrefix('!');
    $routeProvider.otherwise({redirectTo: '/'});
  }])
/**
 * Redirect user to login page if already logged in.
 */
  .run(['CookieService', '$location', '$rootScope', function (CookieService, $location, $rootScope) {
    'use strict';
    var hasCookie = CookieService.checkLoginCookie();
    if (!hasCookie && $location.path() !== '/') {
      $rootScope.$broadcast('event:auth-loginRequired');
    }
    if (hasCookie && $location.path() === '/') {
      $location.path('/landing');
    }
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

