angular.module('irida.project', ['ajaxService'])

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when(
      '/projects/:projectId', {
        templateUrl: './partials/project.html',
        controller: function ($scope, initData) {
          $scope.links = initData.resource.links;
          $scope.project = initData.resource;
        },
        resolve: {
          initData: function ($q, $route, ajaxService) {
            var defer = $q.defer();
            var id = $route.current.params.projectId;
            ajaxService.get('/projects/' + id).then(function (data) {
              defer.resolve(data);
            });
            return defer.promise;
          }
        }
      })
  }])

  .controller('ProjectCtrl', ['$scope', '$route', '$location', 'ajaxService', function ($scope, $route, $location, ajaxService) {
    'use strict';

  }]);