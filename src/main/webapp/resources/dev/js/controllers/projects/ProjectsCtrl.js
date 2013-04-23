/* global angular */
var irida = angular.module('irida', ['ngResource']);
irida.controller(ProjectsListCtrl);

var ProjectsListCtrl = function ($scope, $window, Projects) {
  "use strict";
  $scope.projects = [];
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

  $scope.gotoProject = function(url){
    $window.location = url;
  }

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
    create     : function (data) {
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