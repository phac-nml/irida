/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date: 2013-04-17
 * Time: 9:41 AM
 */

/**
 * This is the user module.
 * It is responsible for the user view.
 */
angular.module('irida')

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    'use strict';
    $routeProvider.when(
      '/users/:username', {
        templateUrl: '/partials/user.html',
        controller: function ($scope, initData) {
          $scope.user = initData.resource;
          $scope.links = initData.resource.links;
        },
        resolve: {
          initData: function ($q, $route, ajaxService) {
            var defer = $q.defer();
            var username = $route.current.params.username;
            ajaxService.get('/api/users/' + username).then(function (data) {
              defer.resolve(data);
            });
            return defer.promise;
          }
        }
      });
  }])

/**
 * User View Controller
 */
  .controller('UserCtrl', ['$rootScope', '$scope', '$location', 'ajaxService', function ($rootScope, $scope, $location, ajaxService) {
    'use strict';

    $scope.deleteUser = function () {
      ajaxService.deleteItem($scope.links.self).then(

        function () {
          $scope.notifier.icon = 'trash';
          $scope.notifier.message = 'Deleted ' + $scope.user.username;
          $rootScope.$broadcast('notify');
          $location.path('/users');
        },

        function () {
          $scope.notifier.icon = 'ban-circle';
          $scope.notifier.message = 'Could not delete ' + $scope.user.username;
          $rootScope.$broadcast('notify');
        });
    };

    $scope.blur = function (name) {
      ajaxService.patch($scope.links.self, '{"' + name + '":"' + $scope.user[name] + '"}').then(

        function () {
          $scope.notifier.icon = 'save';
          $scope.notifier.message = 'Saved ' + name + ': ' + $scope.user[name];
          $rootScope.$broadcast('notify');

        },

        function () {
          console.log('Error figure this out will ya!');
        });
    };
//    };
  }]);