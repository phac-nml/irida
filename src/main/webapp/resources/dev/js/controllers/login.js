/**
 * User: josh
 * Date: 2013-05-09
 * Time: 2:18 PM
 */
angular.module('irida')

/**
 * Configure the route parameters
 * $routeProvider
 */
  .config(['$routeProvider', function ($routeProvider) {
    'use strict';
    $routeProvider.when(
      '/login', {
        templateUrl: './partials/login.html',
        controller: 'LoginCtrl'
      });
  }])
  .controller('LoginCtrl', ['$scope', '$location', '$http','ajaxService', function ($scope, $location, $http, ajaxService) {
    'use strict';
    $scope.login = function () {
      $http.defaults.headers.common['Authorization'] = 'Basic ' + Base64.encode($scope.username + ':' + $scope.password);
      ajaxService.post('/login').then(
        function () {
          $location.path('/');
        },
        function () {
          // TODO: Show a message stating that the login credentials are incorrect.
          $scope.showError = true;
        }
      );
    };
  }]);
