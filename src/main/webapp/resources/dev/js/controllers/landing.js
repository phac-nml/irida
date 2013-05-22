/* global angular */
(function (ng, app) {
  'use strict';
  /**
   * Configure the route parameters
   * $routeProvider
   */
  app.
    config(['$routeProvider', function ($routeProvider) {
      'use strict';
      $routeProvider.when(
        '/', {
          controller: 'LandingCtrl',
          templateUrl: '/partials/landing.html'
        });
    }])
    .controller('LandingCtrl', ['$scope', function ($scope) {
      'use strict';

      // Initialize
      $scope.$emit('setWindowTitle','Landing Page');
    }]);
})(angular, NGS);