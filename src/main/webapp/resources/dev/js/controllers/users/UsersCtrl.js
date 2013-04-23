/* global angular */
var irida = angular.module('irida', ['ngResource']);
irida.controller(UsersListCtrl);

var UsersListCtrl = function ($scope, $window, Users) {
  $scope.users = [];

  $scope.usersUrl = '/users' + '?_' + Math.random();


  $scope.loadUsers = function (url) {
    'use strict';
    Users.getAllUsers(url).then(
      function (data) {
        ajaxSuccessCallback(data);
      },
      function (errorMessage) {
        // TODO: handle error message
      });
  };

  $scope.gotoUser = function (url) {
    $window.location = url;
  };

  $scope.clearForm = function () {
    $scope.newUser = {};
    $scope.errors = {};

    // Need to reset all the fields in the form.
    $('form[name=newUserForm] .ng-dirty').removeClass('ng-dirty').addClass('ng-pristine');
    var form = $scope.newUserForm;
    for (var field in form) {
      if (form[field].$pristine === false) {
        form[field].$pristine = true;
      }
      if (form[field].$dirty === true) {
        form[field].$dirty = false;
      }
    }
    $scope.newUserForm.$pristine = true;
  };

  $scope.submitNewUser = function () {
    if ($scope.newUserForm.$valid) {
      Users.create($scope.newUser).then(
        function () {
          $scope.loadUsers($scope.usersUrl);
          $scope.clearForm();
          $('#newUserModal').foundation('reveal', 'close');
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
    else {
      console.log("NOT VALID");
    }
  };

  function ajaxSuccessCallback(data) {
    "use strict";
    $scope.links = {};
    angular.forEach(data.userResources.links, function (val) {
      $scope.links[val.rel] = val.href;
    });
    $scope.users = data.userResources.users;
  }
};

var NewUserCtrl = function ($scope, Users) {


};

irida.factory('Users', function ($http, $q) {
  "use strict";
  return {
    create     : function (data) {
      var deferred = $q.defer();
      $http({
        method : 'POST',
        url    : '/users',
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
    getAllUsers: function (url) {
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


