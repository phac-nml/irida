/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    9:11 AM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.config(function ($stateProvider, $routeProvider) {
    $stateProvider
      .state('projects', {
        url: '/projects',
        templateUrl: '/partials/landing.html',
        resolve: {
          data: function ($q, ajaxService) {
            var defer = $q.defer();
            ajaxService.get('/api/projects').then(function (data) {
              defer.resolve(data);
            });
            return defer.promise;
          }
        },
        controller: function ($scope, data) {
          $scope.projects = data.resource.resources;
        }
      })
      .state('projects.detail', {
        url: '/:projectId',
        templateUrl: '/partials/landing.project.html',
        resolve: {
          data: function ($q, $stateParams, ajaxService) {
            var defer = $q.defer();
            ajaxService.get('/api/projects/' + $stateParams.projectId).then(function (data) {
              defer.resolve(data);
            });
            return defer.promise;
          }
        },
        controller: function ($scope, data) {
          console.log(data);
          $scope.project = {
            name: data.resource.name,
            users: data.relatedResources.users.resources
          };
        }
      })
      .state('login', {
        url: '/login',

        templateUrl: '/partials/login.html'
      });
  });
})
  (angular, NGS);