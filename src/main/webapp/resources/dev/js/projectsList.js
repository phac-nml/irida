/* global angular */
angular.module('irida.projectsList', ['ajaxService'])

/**
 * Configure the route parameters
 * $routeProvider
 */
.config(['$routeProvider', function ($routeProvider) {
  $routeProvider.when(
    '/projects', {
    templateUrl: './partials/projects.html',
    controller: function ($scope, initData) {
      $scope.projects = initData.resources.resources;
      $scope.links = initData.resources.links;
    },
    resolve: {
      initData: function ($q, ajaxService) {
        var defer = $q.defer();
        ajaxService.get('/projects').then(function (data) {
          defer.resolve(data);
        });
        return defer.promise;
      }
    }
  })
}])
  .controller('ProjectsListCtrl', ['$scope', '$location', 'ajaxService', function ($scope, $location, ajaxService) {
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

      function () {
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

  function ajaxSuccessCallback(data) {
    $scope.links = data.resources.links;
    $scope.projects = data.resources.resources;
  }

}]);