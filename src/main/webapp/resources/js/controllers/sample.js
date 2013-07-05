/* global angular, NGS */
/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-25
 * Time:    2:51 PM
 * License: MIT
 */
(function (ng, app) {
  "use strict";
  app.controller('SampleCtrl', ['$scope', 'projectService', function ($scope, projectService) {
      $scope.data.project = projectService.project;

  }]);
})(angular, NGS);