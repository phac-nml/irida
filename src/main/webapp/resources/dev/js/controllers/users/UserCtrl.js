/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-17
 * Time:   9:41 AM
 */

angular.module('irida', ['ngResource']);

angular.module('irida')
  .controller('UserCtrl', function ($scope, $window, dataStore) {
    'use strict';

    $scope.username = "";
    $scope.user = {};
    $scope.links = {};
    $scope.projects = [];

    $scope.init = function () {
      var username = /\/users\/(.*)$/.exec($window.location.pathname)[1];
      dataStore.getData('/users/' + username).then(
        function (data) {
          initialAjaxCallback(data);

        },
        function (errorMessage) {
          // TODO: handle error message
        });
    };

    function initialAjaxCallback(data) {
      "use strict";
      angular.forEach(data.resource.links, function (val) {
        $scope.links[val.rel] = val.href;
      });
      delete data.resource.links;
      $scope.user = data.resource;
      getUserProjects();
    }

    function getUserProjects () {
      dataStore.getData($scope.links['user/projects']).then(
        function (data) {
          $scope.projects = data.projectResources.projects;

        },
        function (errorMessage) {
          // TODO: handle error message
        });
    }
  });

angular.module('irida')
  .factory('dataStore', function ($http, $q) {
    "use strict";
    return {
      getData: function (url) {
        var deferred = $q.defer();

        $http.get(url)
          .success(function (data) {
            deferred.resolve(data);
          })
          .error(function () {
            deferred.reject("An error occured while getting user data");
          });

        return deferred.promise;
      }
    };
  });