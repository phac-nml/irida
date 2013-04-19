/* global angular */

angular.module('irida')
  .controller('UsersListCtrl', function ($scope, $window, usersData) {
    $scope.usersUrl = '/users' + '?_' + Math.random();
    $scope.users = [];
    $scope.newUser = {};

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