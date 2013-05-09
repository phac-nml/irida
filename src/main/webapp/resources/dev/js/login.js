/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2013-05-09
 * Time: 2:18 PM
 * To change this template use File | Settings | File Templates.
 */
/* global angular */
angular.module('irida.login', [])

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when(
      '/', {
        templateUrl: './partials/login.html',
        controller: 'LoginCtrl'
      })
  }])
  .controller('LoginCtrl', ['$scope', '$location', 'ajaxService', function ($scope, $location, ajaxService) {
    'use strict';
    $scope.login = function () {
      ajaxService.post('/login', {username: $scope.username, password: $scope.password}).then(
        function () {
          $location.path('/landing');
        },
        function () {
          console.log("error")
          // TODO: Show a message stating that the login credentials are incorrect.
          $scope.showError = true;
        }
      );
    };
  }]);
