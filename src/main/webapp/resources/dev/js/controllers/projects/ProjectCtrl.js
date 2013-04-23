angular.module('irida', ['ngResource']);

angular.module('irida')
  .controller('ProjectCtrl', function ($scope, $window, dataStore) {
    'use strict';
    $scope.name = "                ";
    $scope.links = [];

    $scope.init = function () {
      var projectID = /\/projects\/(.*)$/.exec($window.location.pathname)[1];
      dataStore.getData('/projects/' + projectID).then(
        function (data) {
          initialAjaxCallback(data);

        },
        function (errorMessage) {
          // TODO: handle error message
        });
    };

    function initialAjaxCallback(data) {
      "use strict";
      angular.forEach(data.project.links, function (val) {
        $scope.links[val.rel] = val.href;
      });
      delete data.project.links;
      $scope.name = data.project.name;
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