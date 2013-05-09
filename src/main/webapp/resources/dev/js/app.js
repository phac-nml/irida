/* global angular, console, LandingCtrl, ProjectCtrl, ProjectsListCtrl, UsersListCtrl, UserCtrl */

angular.module('irida', [
    'http-auth-interceptor',
    'irida.login',
    'irida.landing',
    'irida.project',
    'irida.projectsList',
    'irida.user',
    'irida.users',
    'irida.directives',
    'logincheck'
  ])
  .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';

    $locationProvider.hashPrefix('!');

    $routeProvider.otherwise({redirectTo: '/'});
  }])
  .run(['logincheck', '$location', function (logincheck, $location) {
    'use strict';
    console.log($location.path());
    if (logincheck.isLoggedIn() && $location.path() === '/') {
      $location.path('/landing');
    }
  }])
  .controller('AppCtrl', ['$scope', 'authService', function ($scope) {
    $scope.notifier = {
      message: '',
      icon: ''
    };
  }])

;

