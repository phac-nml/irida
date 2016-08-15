(function(angular, $, _, TL) {
  'use strict';
  var deps = _.union(window.dependencies || [], [
    'ngAria',
    'ngAnimate',
    'ui.bootstrap',
    'irida.session',
    'irida.notifications',
    'irida.cart'
  ]);

  angular.module('irida', deps)
    .config(function($httpProvider) {
      $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

      // Make sure that all ajax form data is sent in the correct format.
      $httpProvider.defaults.transformRequest = function(data) {
        if (data === undefined) {
          return data;
        }
        return $.param(data);
      };
    });
})(window.angular, window.$, window._, window.TL);