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
    controller: function($scope, $attrs, $element, $location) {
      $scope.logout = function () {
        // TODO: (JOSH - 2013-05-16) Remove http header for authentication
        $location.path('/login');
      };
    },
    templateUrl: './partials/navbar.html',
    link: function() {}
  };
});