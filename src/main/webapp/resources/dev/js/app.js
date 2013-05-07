/* global angular, console, LandingCtrl, ProjectCtrl, ProjectsListCtrl, UsersListCtrl, UserCtrl */

angular.module('irida', [
    'irida.landing',
    'irida.project',
    'irida.projectsList',
    'irida.user',
    'irida.users',
    'irida.directives',
    'ui'])
    .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    'use strict';

    $locationProvider.hashPrefix('!');

    $routeProvider.otherwise({redirectTo: '/'});
}])
  .controller('AppCtrl', ['$scope', function ($scope) {
    $scope.notifier = {
      message: '' ,
      icon: ''
    };
  }])
;

