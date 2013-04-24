/* global angular */
var irida = angular.module('irida');

function ProjectsListCtrl($scope, $window, AjaxService) {
  'use strict';
  $scope.projects = [];
  $scope.newProject = {};
  $scope.errors = {};
  $scope.projectsUrl = '/projects' + '?_' + Math.random();

  $scope.loadProjects = function(url) {
    AjaxService.getAll(url).then(

    function(data) {
      ajaxSuccessCallback(data);
    },

    function(errorMessage) {
      // TODO: handle error message
      console.log(errorMessage);
    });
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
    $window.location = url;
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
    angular.forEach(data.projectResources.links, function(val) {
      $scope.links[val.rel] = val.href;
    });
    $scope.projects = data.projectResources.projects;
  }
}
ProjectsListCtrl.$inject = ['$scope', '$window', 'AjaxService'];
irida.controller(ProjectsListCtrl);