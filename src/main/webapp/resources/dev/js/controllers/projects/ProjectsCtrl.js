/* global angular */
var irida = angular.module('irida');

function ProjectsListCtrl($scope, $location, AjaxService) {
  'use strict';
  $scope.projects = [];
  $scope.links = {};
  $scope.newProject = {};
  $scope.errors = {};
  $scope.projectsUrl = '/projects' + '?_' + Math.random();

  $scope.loadProjects = function(url) {
    if (url) {
      AjaxService.get(url).then(

      function(data) {
        ajaxSuccessCallback(data);
      },

      function(errorMessage) {
        // TODO: handle error message
        console.log(errorMessage);
      });
    }
  };

  $scope.clearForm = function() {
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

  $scope.gotoProject = function(url) {
    $location.path(url.match(/\/projects\/.*$/)[0]);
  };

  $scope.submitNewProject = function() {
    if ($scope.newProjectForm.$valid) {
      AjaxService.create('/projects', $scope.newProject).then(

      function() {
        $scope.loadProjects($scope.projectsUrl);
        $scope.clearForm();
        $('#newProjectModal').foundation('reveal', 'close');
      },

      function(data) {
        $scope.errors = {};
        angular.forEach(data, function(error, key) {
          $scope.errors[key] = data[key].join('</br>');
        });
      });
    }
  };

  function ajaxSuccessCallback(data) {
    $scope.links = {};
    angular.forEach(data.resources.links, function(val) {
      $scope.links[val.rel] = val.href;
    });
    $scope.projects = data.resources.resources;
  }

  var render = function () {
    $scope.loadProjects($scope.projectsUrl);
  };

  $scope.$on('$routeChangeSuccess', function () {
    render();
  });
}
irida.controller(ProjectsListCtrl);

irida.factory('Projects', function($http, $q) {
  'use strict';
  return {
    create: function(data) {
      var deferred = $q.defer();
      $http({
        method: 'POST',
        url: '/projects',
        data: data,
        headers: {
          'Content-Type': 'application/json'
        }
      })
        .success(function(data) {
        deferred.resolve(data);
      })
        .error(function(data) {
        deferred.reject(data);
      });
      return deferred.promise;
    },
    getProjects: function(url) {
      var deferred = $q.defer();

      $http.get(url)
        .success(function(data) {
        deferred.resolve(data);
      })
        .error(function() {
        deferred.reject("An error occurred while getting users");
      });

      return deferred.promise;
    }
  };
});