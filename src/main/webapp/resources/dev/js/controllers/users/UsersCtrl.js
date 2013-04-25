/* global angular */
var irida = angular.module('irida');

function UsersListCtrl($scope, $window, AjaxService) {
  'use strict';
  $scope.users = [];
  $scope.usersUrl = '/users' + '?_' + Math.random();

  $scope.loadUsers = function(url) {
    AjaxService.getAll(url).then(

    function(data) {
      ajaxSuccessCallback(data);
    },

    function(errorMessage) {
      // TODO: handle error message
      console.log(errorMessage);
    });
  };

  $scope.gotoUser = function(url) {
    $window.location = url;
  };

  $scope.clearForm = function() {
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
      if (form[field].$invalid) {
        form[field].$invalid = false;
      }
    }
    $scope.newUserForm.$pristine = true;
  };

  $scope.submitNewUser = function() {
    if ($scope.newUserForm.$valid) {
      AjaxService.create('/users', $scope.newUser).then(

      function() {
        $scope.loadUsers($scope.usersUrl);
        $scope.clearForm();
        $('#newUserModal').foundation('reveal', 'close');
      },

      function(data) {
        $scope.errors = {};
        angular.forEach(data, function(error, key) {
          $scope.errors[key] = data[key].join('</br>');
        });
      });
    } else {
      console.log('NOT VALID');
    }
  };

  function ajaxSuccessCallback(data) {
    $scope.links = {};
    angular.forEach(data.userResources.links, function(val) {
      $scope.links[val.rel] = val.href;
    });
    $scope.users = data.userResources.users;
  }
}
irida.controller(UsersListCtrl);