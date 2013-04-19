/* global angular */

angular.module('irida', ['ngResource', 'ui.bootstrap']);

angular.module('irida')
  .controller('NavCtrl', function ($scope, $dialog) {
    'use strict';

    $scope.openNewUsersModal = function ($event) {
      $event.preventDefault();
      var d = $dialog.dialog($scope.opts);
      d.open('/users/partials/newUserModal.html');
    };

    $scope.opts = {
      backdropFade: true,
      dialogFade:true,
      keyboard: true,
      controller: 'DialogCtrl'
    };
  })
  .controller('DialogCtrl', function($scope, dialog, createUser) {
    "use strict";

    $scope.errors = {};
    $scope.newUser = {};

    $scope.closeNewUsersModal = function() {
      dialog.close();
    };

    $scope.submitNewUser = function () {
      if($scope.newUserForm.$valid) {
        createUser.create($scope.newUser);
      }
      else {
        console.log("NOT VALID");
      }
    };
  }).
  factory('createUser', function ($http) {
    "use strict";
    var addUser = {};

    addUser.create = function (data) {
      $http({
        method: 'POST',
        url: '/users',
        data: data,
        headers: {'Content-Type': 'application/json'}
      })
        .success(function (data, status) {
          console.log("SUCCESS");
        })
        .error(function(data, status) {
          console.log("ERROR");
        });
    };

    return addUser;
  });