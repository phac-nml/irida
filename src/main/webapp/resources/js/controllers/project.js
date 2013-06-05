/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    1:05 PM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.controller('ProjectCtrl', ['$scope', 'ajaxService', '$location', function ($scope, ajaxService, $location) {
//    $scope.project = {};
//    if ($stateParams.projectId) {
//      ajaxService.get('/api/projects/' + $stateParams.projectId).then(function (data) {
//        $scope.project.name = data.resource.name;
//        $scope.project.users = data.relatedResources.users.resources;
//        console.log(data);
//      });
//    }
//    else {
//      // todo: what to do now... no project id.
//    }
//    $scope.$on('CHANGE_PROJECT', function (event, url) {
//      ajaxService.get(url).then(function(data){
//        $scope.project = {
//          name: data.resource.name,
//          users: data.relatedResources.users.resources
//        };
//      });
//    });
  }]);
})(angular, NGS);