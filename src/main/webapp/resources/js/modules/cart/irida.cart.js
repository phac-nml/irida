(function () {
    "use strict";
    // TODO (Josh - 14-12-22) Refactor to directive
    function cartController() {
        "use strict";
        var vm = this;
        vm.show = false;
        vm.projects = [];
    }

    angular
        .module('irida.cart', [])
        .controller('cartController', [cartController]);
    ;
})();