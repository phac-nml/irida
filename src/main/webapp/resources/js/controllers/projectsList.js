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
      // TODO: (Josh: 2013-06-12) Show notification of successfull delete
      for (var i = 0; i < $scope.projects.length; i++) {
        if ($scope.projects[i].name === args.name) {
          $scope.projects.splice(i, 1);
          break;
        }
      }
    });

    $scope.createProject = function () {
      alert('You are creating a project');
      // TODO: (Josh: 2013-06-14) Create modal window to facilitate new project. 
    };

    $scope.gotoProject = function (url) {
      $location.path(url.match(/\/projects\/.*$/)[0]);
    };

  }]);
})(angular, NGS);