/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    1:05 PM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.controller('ProjectCtrl', ['$scope', '$rootScope', 'ajaxService', '$location', '$timeout', function ($scope, $rootScope, ajaxService, $location, $timeout) {
    $scope.samples = {};

    $scope.deleteProject = function () {
      ajaxService.deleteItem($scope.project.links.self).then(function() {
        // TODO: Show notification and undo of delete
        $rootScope.$broadcast('PROJECT_DELETED', {'name':$scope.project.name});
        $location.path('/projects');
      });
    };

    $scope.loadSample = function (s) {
//      ajaxService.get(s.links[0].href).then(function (data) {
//        console.log(data);
//        $scope.samples[s.label] = {'loaded':'loaded'};
//      });
//      $scope.samples[s.label] = {'loaded':'loaded'};
      $timeout(function () {
        $scope.samples[s.label] = {'loaded':'loaded'};
      }, 500);
    };
  }]);
})(angular, NGS);