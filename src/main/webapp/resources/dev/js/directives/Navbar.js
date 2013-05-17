/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-05-16
 * Time:   8:19 AM
 */

angular.module('irida')
  .directive('ngsNavbar', function () {
    'use strict';
    return {
      restrict: 'E',
      replace: true,
      controller: function ($scope, $attrs, $element, $location, loginService) {
        $scope.nav = {
          hide: $location.path() === '/login'
        };
        // Hide navigation if originally on login page
        $scope.$on('$routeChangeStart', function() {
//          var path = $location.path().match();
          $scope.nav.hide = $location.path() === '/login';
        });

        $scope.logout = function () {
          loginService.deleteHeader();
          $location.path('/login');
        };
      },
      link: function (scope, el) {
        el.foundation('topbar');
      },
      templateUrl: '/partials/navbar.html'
    };
  });