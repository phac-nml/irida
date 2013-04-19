/* global angular */

angular.module('irida')
  .controller('UsersListCtrl', function ($scope, $window, $dialog, $tooltip, usersData) {
    $scope.usersUrl = '/users' + '?_' + Math.random();
    $scope.users = [];
    $scope.newUser = {};

    $scope.openNewUsersModal = function () {
      var d = $dialog.dialog($scope.opts);
      d.open('/users/partials/newUserModal.html');
    };

    $scope.opts = {
      backdropFade: true,
      dialogFade:true,
      keyboard: true,
      controller: 'DialogCtrl'
    };

    $scope.loadUsers = function (url) {
      'use strict';
      usersData.getAllUsers(url).then(
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

    $scope.init = function () {
      'use strict';
         // TODO: EXTRACT THIS

      $scope.loadUsers($scope.usersUrl);
    };

    function ajaxSuccessCallback(data) {
      "use strict";
      $scope.links = {};
      angular.forEach(data.userResources.links, function (val) {
        $scope.links[val.rel] = val.href;
      });
      $scope.users = data.userResources.users;
    }
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
          console.log(data);
        });
    };

    return addUser;
  });

angular.module('irida')
  .factory('usersData', function ($http, $q) {
    "use strict";
    return {
      getAllUsers: function (url) {
        var deferred = $q.defer();

        $http.get(url)
          .success(function (data) {
            deferred.resolve(data);
          })
          .error(function () {
            deferred.reject("An error occured while getting users");
          });

        return deferred.promise;
      }
    };
  });