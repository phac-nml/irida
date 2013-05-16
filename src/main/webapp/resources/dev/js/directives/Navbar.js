/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-05-16
 * Time:   8:19 AM
 */

angular.module('irida')
  .directive('ngsNavbar', function() {
  'use strict';
  return {
    restrict: 'E',
    replace: true,
    controller: function($scope, $attrs, $element, $location, $http) {
      $scope.logout = function () {
        $http.defaults.headers.common['Authorization'] = '';
        $location.path('/login');
      };
    },
    templateUrl: './partials/navbar.html'
  };
});