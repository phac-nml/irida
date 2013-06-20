/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    9:11 AM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.config(function ($stateProvider) {
    $stateProvider
    /**
     * Landing Page
     */
      .state('projects', {
        url: '/',
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
          formatObjectLinks(data);
          $scope.projects = data.resource.resources;
        }
      })
      .state('projects.main', {
        url: 'landing',
        templateUrl: '/partials/default-landing.html'
      })
    /**
     * Project Page
     */
      .state('projects.detail', {
        url: 'projects/:projectId',
        templateUrl: '/partials/project.html',
        resolve: {
          data: function ($q, $stateParams, ajaxService) {
            var defer = $q.defer();
            ajaxService.get('/api/projects/' + $stateParams.projectId).then(function (data) {
              defer.resolve(data);
            });
            return defer.promise;
          }
        },
        controller: function ($scope, $stateParams, data) {
          formatObjectLinks(data);
          console.log(data);
          $scope.project = {
            id: $stateParams.projectId,
            name: data.resource.name,
            users: data.relatedResources.users.resources,
            samples: data.relatedResources.samples.resources,
            sequenceFiles: data.relatedResources.sequenceFiles.resources,
            links: data.resource.links
          };
        }
      })

      .state('login', {
        url: '/login',
        templateUrl: '/partials/login.html'
      });
  });

  function formatObjectLinks(obj) {
    for (var key in obj) {
      if (key === 'links') {
        obj[key] = linkFormatter(obj[key]);
      }
      else if (typeof obj[key] === 'object') {
        formatObjectLinks(obj[key]);
      }
    }
  }

  function linkFormatter(links) {
    var l = {};
    for (var i = 0; i < links.length; i++) {
      l[links[i].rel] = links[i].href;
    }
    return l;
  }
})
  (angular, NGS);