/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    9:51 AM
 * License: MIT
 */

(function (ng, app) {
  'use strict';
//  app.config(['$routeProvider', function ($routeProvider) {
//    $routeProvider.when('/projects/:projectId', {
//      templateUrl: '/partials/landing.html',
//      controller: function ($scope, data) {
//        debugger;
//        $scope.projects = data.resource.resources;
//      },
//      resolve: {
//        data: function ($q, ajaxService) {
//          debugger;
//          var defer = $q.defer();
//          ajaxService.get('/api/projects').then(function (data) {
//            defer.resolve(data);
//          });
//          return defer.promise();
//        }
//      }
//    });
//  }]);

  app.controller('ProjectsListCtrl', ['$scope', 'ajaxService', '$location', function ($scope, ajaxService, $location) {
//    $scope.projects = [];

//    ajaxService.get('/api/projects').then(function (data) {
//      $scope.projects = data.resource.resources;
//    });

    $scope.gotoProject = function (url) {
//      console.log(url.match(/projecets\/(.*)$/);
      $location.path(url.match(/\/projects\/.*$/)[0] + '/samples');
//      $scope.$broadcast('CHANGE_PROJECT', url);
    };
  }]);
})(angular, NGS);