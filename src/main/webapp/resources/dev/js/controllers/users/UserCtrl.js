/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date: 2013-04-17
 * Time: 9:41 AM
 */

var irida = angular.module('irida');


function UserCtrl($scope, $route, $location, AjaxService) {
  'use strict';

  $scope.user = {};
  $scope.original = {};
  $scope.links = {};
  $scope.projects = [];

  $scope.deleteUser = function () {
    console.log("Deleting user");
    AjaxService.delete($scope.links.self + 'fred').then(
      function () {
        console.log("Success delete");
      },errorHandler);
  };

  /**
   * Checks the editUserForm to see if any of the fields have been
   * modified.  if they have, it adds them to an associative array
   * and performs a PATCH for the current user.
   */
  $scope.patchUser = function () {
    console.log("Patching users");
    var data = {};
    var form = $scope.editUserForm;
    for (var field in form) {
      if (form[field].$dirty === true) {
        data[form[field].$name] = form[field].$modelValue;
      }
    }
    if (data) {
      AjaxService.patch($window.location.pathname, data);
    }
  };

  $scope.blur = function (name) {
    console.log('BLUR: ' + name);
  };

  /**
   * Initial callback to setup the interface
   * @param {object} data
   */
  var render = function () {
    var username = $route.current.params.username;
    AjaxService.get('/users/' + username).then(
      function (data) {
        angular.forEach(data.resource.links, function (val) {
          $scope.links[val.rel] = val.href;
        });
        delete data.resource.links;
        $scope.user = data.resource;
        $scope.original = angular.copy($scope.user);

        AjaxService.get($scope.links['user/projects']).then(

          function (data) {
            $scope.projects = data.projectResources.projects;

          },

          function (errorMessage) {
            // TODO: handle error message
          });
      },
      function () {
        alert("NEED TO SET UP AJAX ERROR NOTIFIERS");
      }
    );


  };

  $scope.$on('$routeChangeSuccess', function () {
     render();
  });
}
irida.controller(UserCtrl);