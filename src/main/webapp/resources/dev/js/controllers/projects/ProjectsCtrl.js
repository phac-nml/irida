/* global angular */
var irida = angular.module('irida', ['ngResource']);
irida.controller(ProjectsListCtrl);

var ProjectsListCtrl = function ($scope, $window, Projects) {
  "use strict";
  $scope.projects = [];
  $scope.newProject = {};
  $scope.errors = {};
  $scope.projectsUrl = '/projects' + '?_' + Math.random();

  $scope.loadProjects = function (url) {
    'use strict';
    Projects.getAllProjects(url).then(
      function (data) {
        ajaxSuccessCallback(data);
      },
      function (errorMessage) {
        // TODO: handle error message
        console.log("An error occurred");
      });
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
    $window.location = url;
  };

  function ajaxSuccessCallback(data) {
    "use strict";
    $scope.links = {};
    angular.forEach(data.resources.links, function (val) {
      $scope.links[val.rel] = val.href;
    });
    $scope.projects = data.resources.resources;
  }
};

var NewProjectCtrl = function ($scope) {
  "use strict";
  var modal = $('#newProjectModal');

  modal.foundation('reveal', {
    closed: function () {
      // Update the current list of users
      $scope.loadProjects($scope.links.self);

      $scope.$apply(function () {
        $scope.newProject = {};
        $scope.errors = {};

        // Need to reset all the fields in the form.
        $('form[name=newProjectForm] .ng-dirty').removeClass('ng-dirty').addClass('ng-pristine');
        var form = $scope.newUserForm;
        for (var field in form) {
          if (form[field].$pristine === false) {
            form[field].$pristine = true;
          }
          if (form[field].$dirty === true) {
            form[field].$dirty = false;
          }
        }
        $scope.newProjectForm.$pristine = true;
      });
    }
  });

  $scope.submitNewProject = function () {
    if ($scope.newProjectForm.$valid) {
      Projects.create($scope.newProject).then(
        function () {
          $scope.loadProjects($scope.projectsUrl);
          $scope.clearForm();
          $('#newProjectModal').foundation('reveal', 'close');
        },
        function (data) {
          $scope.errors = {};
          angular.forEach(data, function (error, key) {
            "use strict";
            $scope.errors[key] = data[key].join("</br>");
          });
        }
      );
    }
  };

  function ajaxSuccessCallback(data) {
    "use strict";
    $scope.links = {};
    angular.forEach(data.projectResources.links, function (val) {
      $scope.links[val.rel] = val.href;
    });
    $scope.projects = data.projectResources.projects;
  }
};

irida.factory('Projects', function ($http, $q) {
  "use strict";
  return {
    create        : function (data) {
      var deferred = $q.defer();
      $http({
        method : 'POST',
        url    : '/projects',
        data   : data,
        headers: {'Content-Type': 'application/json'}
      })
        .success(function (data) {
          deferred.resolve(data);
        })
        .error(function (data) {
          deferred.reject(data);
        });
      return deferred.promise;
    },
    getAllProjects: function (url) {
      var deferred = $q.defer();

      $http.get(url)
        .success(function (data) {
          deferred.resolve(data);
        })
        .error(function () {
          deferred.reject("An error occurred while getting users");
        });

      return deferred.promise;
    }
  };
});
