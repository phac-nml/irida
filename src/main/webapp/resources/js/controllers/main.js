/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    8:55 AM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.
    controller('MainCtrl', ['$scope', '$location', 'loginService', function ($scope, $location, loginService) {

      $scope.sidebar = {
        visible: true,
        widthExpanded: '15em',
        widthCollapsed: '4.5em'
      };

      $scope.$on('event:auth-loginRequired', function () {
        $location.path('/login');
      });

      $scope.logout = function () {
        loginService.deleteHeader();
        $location.path('/login');
      };

      $scope.toggleProjectList = function () {
        if ($scope.sidebar.visible) {
          $('.sidebar__body').fadeOut('fast');
          $('.sidebar__rotated').fadeIn('slow');
          $('.sidebar').width($scope.sidebar.widthCollapsed);
        }
        else {
          $('.sidebar').width($scope.sidebar.widthExpanded);
          $('.sidebar__rotated').fadeOut('fast');
          $('.sidebar__body').fadeIn('slow');
        }
        $scope.sidebar.visible = !$scope.sidebar.visible;
      };
    }]);
})(angular, NGS);