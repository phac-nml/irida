/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    1:05 PM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.controller('ProjectCtrl', ['$scope', 'ajaxService', '$location', '$timeout', function ($scope, ajaxService, $location, $timeout) {
    $scope.samples = {};
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