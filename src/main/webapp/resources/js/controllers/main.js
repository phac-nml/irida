/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    8:55 AM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.
    controller('MainCtrl', ['$scope', '$location', 'loginService', function ($scope, $location, loginService) {
      $scope.$on('event:auth-loginRequired', function () {
        $location.path('/login');
      });

      $scope.logout = function () {
        loginService.deleteHeader();
        $location.path('/login');
      };
    }]);
})(angular, NGS);