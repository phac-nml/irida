(function(angular, $, _, TL) {
  'use strict';
  var deps = _.union(window.dependencies || [], [
    'ngAria',
    'ngAnimate',
    'ngMessages',
    'ui.bootstrap',
    'restangular',
    'irida.session',
    'irida.notifications',
    'irida.cart'
  ]);

  function UIGalaxyController() {
    var vm = this;

    //A page is in Galaxy if it's in an iframe and IRIDA was accessed from Galaxy
    vm.inGalaxy = inIframe() && TL.galaxyCallback;

    function inIframe() {
      try {
        return window.self !== window.top;
      } catch (e) {
        return true;
      }
    }
  }

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
    })
    .run(function(paginationConfig) {
      paginationConfig.firstText = TL.lang.page.first;
      paginationConfig.previousText = TL.lang.page.prev;
      paginationConfig.nextText = TL.lang.page.next;
      paginationConfig.lastText = TL.lang.page.last;
      paginationConfig.boundaryLinks = true;
      paginationConfig.directionLinks = true;
      paginationConfig.maxSize = 8;
      paginationConfig.rotate = false;
    })
    .controller('UIGalaxyController', [UIGalaxyController]);
})(window.angular, window.$, window._, window.TL);