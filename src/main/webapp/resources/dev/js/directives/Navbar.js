/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-05-16
 * Time:   8:19 AM
 */

angular.module('NGS')
  .directive('ngsNavbar', function () {
    'use strict';
    return {
      restrict: 'E',
      replace: true,
      controller: function ($scope, $attrs, $element, $route, $location, loginService) {
        var re = /^\/([^\/]+)/;
        $scope.nav = {
          hide: $location.path() === '/login',
          loc: $location.path().match(re)[1]
        };
        // Hide navigation if originally on login page
        $scope.$on('$routeChangeStart', function() {
          $scope.nav.hide = $location.path() === '/login';
          $scope.nav.loc = $location.path().match(re)[1];
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