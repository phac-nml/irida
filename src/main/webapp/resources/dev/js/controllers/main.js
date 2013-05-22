/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2013-05-15
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
(function (ng, app) {
  'use strict';
  app.
    controller('MainCtrl', ['$scope', '$location', function ($scope, $location) {
      'use strict';
      $scope.notifier = {};

      // Window Title
      $scope.$on('setWindowTitle', function (event, title) {
        $scope.windowTitle = title;
      });
    }]);
})(angular, NGS);