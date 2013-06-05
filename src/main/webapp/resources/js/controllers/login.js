/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    10:19 AM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  /**
   * Configure the route parameters
   * $routeProvider
   */
  app
    .controller('LoginCtrl', ['$scope', '$location', 'loginService', function ($scope, $location, loginService) {
      $scope.login = function () {
        loginService.setHeader($scope.username, $scope.password);
        $location.path('/');
      };
    }]);
})(angular, NGS);