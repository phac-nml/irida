/**
 * User: josh
 * Date: 2013-05-09
 * Time: 2:18 PM
 */
angular.module('irida')

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    'use strict';
    $routeProvider.when(
      '/login', {
        templateUrl: './partials/login.html',
        controller: 'LoginCtrl'
      });
  }])
  .controller('LoginCtrl', ['$scope', '$location', function ($scope, $location) {
    'use strict';
    $scope.login = function () {
      if ($scope.loginForm.$valid) {
//      $httpProvider.defaults.headers.common['Authorization'] = 'Basic ' + Base64.encode($scope.username + ':' + $scope.password);
        $location.path('/');
      }
    };
  }]);
