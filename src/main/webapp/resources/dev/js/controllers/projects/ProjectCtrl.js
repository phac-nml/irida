var irida = angular.module('irida');

function ProjectCtrl($scope, $route, $location, AjaxService) {
    'use strict';
    $scope.links = []
    $scope.project = {};

//    $scope.init = function() {
//        var projectID = /\/projects\/(.*)$/.exec($window.location.pathname)[1];
//        dataStore.getData('/projects/' + projectID).then(
//                function(data) {
//                    initialAjaxCallback(data);
//
//                },
//                function(errorMessage) {
//// TODO: handle error message
//                });
//    };
//
//    function initialAjaxCallback(data) {
//        "use strict";
//        angular.forEach(data.resource.links, function(val) {
//            $scope.links[val.rel] = val.href;
//        });
//        delete data.resource.links;
//        $scope.name = data.resource.name;
//    }

  var render = function () {
    var id = $route.current.params.projectId;
    AjaxService.get('/projects/' + id).then(
      function(data) {
        $scope.project = data.resource;
      },

      function(errorMessage) {
        // TODO: handle error message
        console.log(errorMessage);
      });
  };

  $scope.$on('$routeChangeSuccess', function () {
    render();
  });
}
irida.controller(ProjectCtrl);