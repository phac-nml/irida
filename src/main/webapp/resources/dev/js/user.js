/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date: 2013-04-17
 * Time: 9:41 AM
 */

/**
 * This is the user module.
 * It is responsible for the user view.
 */
var irida = angular.module('irida.user', [
    'ajaxService',
    'messagingService'
  ])

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when(
      '/users/:username', {
        templateUrl: './partials/user.html',
        controller: 'UserCtrl'
      })
  }])

/**
 * User View Contoller
 */
  .controller('UserCtrl', ['$scope', '$route', '$location', 'ajaxService', 'messagingService', function ($scope, $route, $location, ajaxService, messagingService) {
    'use strict';

    $scope.user = {};
    $scope.links = {};
    $scope.projects = [];

    $scope.deleteUser = function () {
      ajaxService.delete($scope.links.self).then(
        function () {
          $scope.notifier.icon = "trash";
          $scope.notifier.message = "Deleted " + $scope.user.username;
          messagingService.broadcast('notify');
          $location.path('/users');
        },
        function () {
          $scope.notifier.icon = 'ban-circle';
          $scope.notifier.message = 'Could not delete ' + $scope.user.username;
          messagingService.broadcast('notify');
        });
    };

    $scope.handleEnter = function ($event) {
      $event.currentTarget.blur();
    };

    $scope.blur = function (name) {
      var form = $scope.editUserForm;
      if (form[name].$invalid) {
        console.log("NOT VALIDE");
      }
      else if ($scope.user[name] != $scope.original[name]) {
        ajaxService.patch($scope.links.self, '{"' + name + '":"' + $scope.user[name] + '"}').then(
          function (data) {
            $scope.notifier.icon = "save";
            $scope.notifier.message = "Saved " + name + ": " + $scope.user[name];
            messagingService.broadcast('notify');

            // Update the original
            $scope.original[name] = $scope.user[name];
          },
          function () {
            console.log("ERROR");
          }
        );
      }
    };

    /**
     * Initial callback to setup the interface
     */
    var render = function () {
      var username = $route.current.params.username;
      ajaxService.get('/users/' + username).then(
        function (data) {
          angular.forEach(data.resource.links, function (val) {
            $scope.links[val.rel] = val.href;
          });
          delete data.resource.links;
          $scope.user = data.resource;
          $scope.original = angular.copy($scope.user);

          ajaxService.get($scope.links['user/projects']).then(

            function (data) {
//            $scope.projects = data.resources.resource;

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
  }]);