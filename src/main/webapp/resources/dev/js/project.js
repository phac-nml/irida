angular.module('irida.project', ['ajaxService'])

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when(
      '/projects/:projectId', {
        templateUrl: './partials/project.html',
        controller: 'ProjectCtrl'
      })
  }])

.controller('ProjectCtrl', ['$scope', '$route', '$location', 'ajaxService', function ($scope, $route, $location, ajaxService) {
    'use strict';
    $scope.links = {};
    $scope.project = {};

    var getUsers = function () {
      console.log($scope.links['project/users']);
      ajaxService.get($scope.links['project/users']).then(
        function (data) {
          $scope.users = data.resource.resources;
        },
        function (data, status, headers, config) {
          console.log(data);
        });
    }

    var render = function () {
      var id = $route.current.params.projectId;
      ajaxService.get('/projects/' + id).then(
        function (data) {
          $scope.links = {};
          angular.forEach(data.resource.links, function (val) {
            $scope.links[val.rel] = val.href;
            console.log(val.rel + ' > ' + val.href);
          });
          $scope.project = data.resource;

          getUsers();
        },

        function (errorMessage) {
          // TODO: handle error message
          console.log(errorMessage);
        });
    };

    $scope.$on('$routeChangeSuccess', function () {
      render();
    });
  }]);