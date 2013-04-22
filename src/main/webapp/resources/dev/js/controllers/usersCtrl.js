/* global angular */

angular.module('irida')
  .controller('UsersListCtrl', function ($scope, $window, usersData, createUser) {
    var modal =  $('#newUserModal');

    $scope.usersUrl = '/users' + '?_' + Math.random();
    $scope.users = [];
    $scope.newUser = {};

    modal.foundation('reveal', {
      closed: function () {
        $scope.$apply( function () {
          $scope.newUser = {};
          $scope.errors = {};

          $('form[name=newUserForm] .ng-dirty').removeClass('ng-dirty').addClass('ng-pristine');
          $scope.newUserForm.$pristine = true;
        });
      }
    });

    $scope.$on('updateUserList', function () {
      $scope.loadUsers($scope.links.self);
    });

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

    $scope.submitNewUser = function () {
      if ($scope.newUserForm.$valid) {
        createUser.create($scope.newUser).then(
          function () {
            modal.foundation('reveal', 'close');
          },
          function (data) {
            $scope.errors = {};
            angular.forEach(data, function(error, key) {
              "use strict";
              $scope.errors[key] =  data[key].join("</br>");
            });
          }
        );
      }
      else {
        console.log("NOT VALID");
      }
    };

    $scope.gotoUser = function (url) {
      $window.location = url;
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
  .factory('createUser',function ($http, $q) {
    "use strict";
    return {
      create: function (data) {
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
      }
    };
  })
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
            deferred.reject("An error occurred while getting users");
          });

        return deferred.promise;
      }
    };
  });