/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-25
 * Time:    1:54 PM
 * License: MIT
 */
(function (ng, app) {
  "use strict";
  app.controller('UserCtrl', ['$scope', function ($scope) {
    $scope.data.edit = false;

    $scope.cancelEdit = function () {
      alert('This needs to be finished');
      $scope.data.edit = false;
    };

    $scope.saveUser = function () {
      alert('This needs to be finished');
    };

    $scope.deleteUser = function () {
      alert('This needs to be finished');
    };

  }]);
})(angular, NGS)