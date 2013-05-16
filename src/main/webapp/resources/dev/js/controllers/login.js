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
  .controller('LoginCtrl', ['$scope', '$location', 'loginService', function ($scope, $location, loginService) {
    'use strict';
    $scope.login = function () {
      loginService.setHeader($scope.username, $scope.password);
      $location.path('/');
    };
  }]);
