/**
 * User: josh
 * Date: 2013-05-09
 * Time: 2:18 PM
 */

(function (ng, app) {
  'use strict';
  /**
   * Configure the route parameters
   * $routeProvider
   */
  app.
    config(['$routeProvider', function ($routeProvider) {
      $routeProvider.when(
        '/login', {
          templateUrl: './partials/login.html',
          controller: 'LoginCtrl'
        });
    }])
    .controller('LoginCtrl', ['$scope', '$location', 'loginService', function ($scope, $location, loginService) {
      $scope.login = function () {
        loginService.setHeader($scope.username, $scope.password);
        $location.path('/');
      };
    }]);
})(angular, NGS);