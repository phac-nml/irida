/*global angular */
angular.module('irida', [
    'ui',
    'http-auth-interceptor',
    'ajaxService',
    'CookieService'
  ])
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';
    $locationProvider.html5Mode(true);
    $routeProvider.otherwise({redirectTo: '/'});
  }])
/**
 * Redirect user to login page if already logged in.
 */
  .run(['CookieService', '$location', '$rootScope', function (CookieService, $location, $rootScope) {
    'use strict';
    var hasCookie = CookieService.checkLoginCookie();
    if (!hasCookie && $location.path() !== '/login') {
      $rootScope.$broadcast('event:auth-loginRequired');
    }
    if (hasCookie && $location.path() === '/login') {
      $location.path('/');
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

