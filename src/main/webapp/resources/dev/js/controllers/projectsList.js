/* global angular */
(function (ng, app) {
  'use strict';
  /**
   * Configure the route parameters
   * $routeProvider
   */
  app
    .config(['$routeProvider', function ($routeProvider) {
      'use strict';
      $routeProvider.when(
        '/projects', {
          templateUrl: './partials/projects.html',
          controller: function ($scope, resourceService, initData) {
            $scope.projects = initData.resource.resources;
            $scope.links = resourceService.formatResourceLinks(initData.resource.links);
          },
          resolve: {
            initData: function ($q, ajaxService) {
              var defer = $q.defer();
              ajaxService.get('/api/projects?size=21&sortOrder=ASCENDING').then(function (data) {
                defer.resolve(data);
              });
              return defer.promise;
            }
          }
        });
    }])
    .controller('ProjectsListCtrl', ['$rootScope', '$scope', '$location', 'ajaxService', 'resourceService', function ($rootScope, $scope, $location, ajaxService, resourceService) {
      'use strict';

      $scope.loadProjects = function (url) {
        if (url) {
          ajaxService.get(url).then(

            function (data) {
              ajaxSuccessCallback(data);
            },

            function (errorMessage) {
              // TODO: handle error message
              console.log(errorMessage);
            });
        }
      };

      $scope.clearForm = function () {
        $scope.newProject = {};
        $scope.errors = {};

        // Need to reset all the fields in the form.
        $('form[name=newProjectForm] .ng-dirty').removeClass('ng-dirty').addClass('ng-pristine');
        var form = $scope.newProjectForm;
        for (var field in form) {
          if (form[field].$pristine === false) {
            form[field].$pristine = true;
          }
          if (form[field].$dirty === true) {
            form[field].$dirty = false;
          }
        }
        $scope.newProjectForm.$pristine = true;
      };

      $scope.gotoProject = function (url) {
        $location.path(url.match(/\/projects\/.*$/)[0]);
      };

      $scope.submitNewProject = function () {
        if ($scope.newProjectForm.$valid) {
          ajaxService.create('/projects', $scope.newProject).then(

            function (link) {
              $scope.notifier.message = 'Created: ' + $scope.newProject.name;
              $scope.notifier.icon = 'check';
              $scope.notifier.link = link;
              $rootScope.$broadcast('notify');
              $scope.loadProjects($scope.projectsUrl);
              $scope.clearForm();
              $('#newProjectModal').foundation('reveal', 'close');
            },

            function (data) {
              $scope.errors = {};
              angular.forEach(data, function (error, key) {
                $scope.errors[key] = data[key].join('</br>');
              });
            });
        }
      };

      // NEW PROJECT MODAL
      $scope.openModal = function () {
        console.log('open seseme');
        $('#newProjectModal').foundation('reveal', 'open')
      };

      // INFINITE SCROLL
      $scope.scroll = {
        disabled: false,
        check: true
      };

      $scope.loadMore = function () {
        if ($scope.scroll.check && $scope.links.next) {
          $scope.scroll.check = false;
          var url = $scope.links.next;
          ajaxService.get(url).then(

            function (data) {
              ajaxSuccessCallback(data);
            },

            function (errorMessage) {
              // TODO: handle error message
              console.log(errorMessage);
            });
        }
        else {
          $scope.scroll.disabled = true;
        }
      };

      function ajaxSuccessCallback(data) {
        $scope.links = {};
        $scope.links = resourceService.formatResourceLinks(data.resource.links);
        console.log($scope.links);
//        $scope.projects = data.resource.resources;
        angular.forEach(data.resource.resources, function (p) {
          $scope.projects.push(p);
        });
        $scope.scroll.check = true;
      }

      // Initialize
      $scope.$emit('setWindowTitle', 'All Projects');

    }]);
})(angular, NGS);