(function () {
    "use strict";
    var deps = _.union(window.dependencies || [], [
        'ngAria',
        'ngResource',
        'ngAnimate',
        'ngMessages',
        'ui.bootstrap',
        'restangular',
        'irida.session',
        'irida.notifications',
        'irida.cart'
    ]);

    angular.module('irida', deps)
        // This configures the base url globally for all restangular calls.
        .config(function (RestangularProvider) {
            RestangularProvider.setBaseUrl(TL.BASE_URL);
        })
        .run(function (paginationConfig) {
            paginationConfig.firstText = TL.lang.page.first;
            paginationConfig.previousText = TL.lang.page.prev;
            paginationConfig.nextText = TL.lang.page.next;
            paginationConfig.lastText = TL.lang.page.last;
            paginationConfig.boundaryLinks = true;
            paginationConfig.directionLinks = true;
            paginationConfig.maxSize = 8;
            paginationConfig.rotate = false;
        })
    ;
})();