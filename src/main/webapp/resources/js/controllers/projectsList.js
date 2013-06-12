/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    9:51 AM
 * License: MIT
 */

(function (ng, app) {
  'use strict';

  app.controller('ProjectsListCtrl', ['$scope', 'ajaxService', '$location', function ($scope, ajaxService, $location) {

    $scope.$on('PROJECT_DELETED', function (event, args) {
      for (var i = 0; i < $scope.projects.length; i++) {
        if ($scope.projects[i].name === args.name) {
          $scope.projects.splice(i, 1);
        }
      }
    });

    $scope.gotoProject = function (url) {
      $location.path(url.match(/\/projects\/.*$/)[0]);
    };

    $scope.$on('PROJECT_DELETED', function () {
      console.log('DELTE');
    });
  }]);
})(angular, NGS);