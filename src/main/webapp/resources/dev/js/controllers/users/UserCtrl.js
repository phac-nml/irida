/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date: 2013-04-17
 * Time: 9:41 AM
 */

var irida = angular.module('irida');


function UserCtrl($scope, $window, AjaxService) {
  'use strict';

  $scope.user = {};
  $scope.original = {};
  $scope.links = {};
  $scope.projects = [];

  $scope.init = function () {
    var username = /\/users\/(.*)$/.exec($window.location.pathname)[1];

    AjaxService.get('/users/' + username).then(initialSuccessCallback, errorHandler);
  };

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

  /**
   * Initial callback to setup the interface
   * @param {object} data
   */
  function initialSuccessCallback(data) {
    angular.forEach(data.resource.links, function (val) {
      $scope.links[val.rel] = val.href;
    });
    delete data.resource.links;
    $scope.user = data.resource;
    $scope.original = angular.copy($scope.user);
    getUserProjects();
  }

  function errorHandler (status){
    console.log(status);
  }

  function getUserProjects() {
    AjaxService.get($scope.links['user/projects']).then(

      function (data) {
        $scope.projects = data.projectResources.projects;

      },

      function (errorMessage) {
        // TODO: handle error message
      });
  }
}
irida.controller(UserCtrl);