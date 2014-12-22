(function () {
    "use strict";
    // TODO (Josh - 14-12-22) Refactor this into a directive.
    function breadcrumbController($window) {
        var vm = this, frags = $window.location.pathname.split("/");
        // Need to find the start
        var found = false;
        while (!found && frags.length > 0) {
            if (TL.lang.crumbs[frags[0]]) {
                found = true;
            }
            else {
                frags.shift();
            }
        }
        vm.crumbs = [];

        // Get root element
        var url = TL.BASE_URL;
        while (frags.length > 1) {
            var name = frags.shift();
            if (!TL.lang.crumbs[name]) {
                break;
            }

            url += name;
            name = TL.lang.crumbs[name] || name;
            vm.crumbs.push({url: url, text: name});

            if (frags.length) {
                var num = frags.shift();
                url += "/" + num;
                vm.crumbs.push({url: url, text: num});
            }
            url += "/";
        }
    }

    angular
        .module('irida')
        .controller('BreadcrumbController', ['$window', breadcrumbController]);
    ;
})();