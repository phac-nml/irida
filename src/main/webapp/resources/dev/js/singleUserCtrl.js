/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-17
 * Time:   9:41 AM
 */

angular.module('irida')
  .controller('UserCtrl', function ($scope, $window, dataStore) {
    "use strict";
    var username = document.getElementById('username').innerText;

    $scope.user = {};
    $scope.links = {};
    $scope.projects = [];

    $scope.init = function () {
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
      angular.forEach(data.user.links, function (val) {
        $scope.links[val.rel] = val.href;
      });
      delete data.user.links;
      $scope.user = data.user;
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

//var username = $('#username').text();
//
//function Project (data) {
//  "use strict";
//   this.name = data.name;
//}
//
//function UserViewModel () {
//  "use strict";
//  self.projects = ko.observableArray([]);
//
//  function getUserProjects () {
//    "use strict";
//
//    $.getJSON('/users/' + username + '/projects', function (allData) {
//      var mappedProjects = $.map(allData.projectResources.projects, function (item) {
//        return new Project(item)
//      });
//      self.projects(mappedProjects);
//    });
//  }
//
//  getUserProjects();
//}
//
//$.ajaxSetup({ cache: false });
//ko.applyBindings(new UserViewModel());