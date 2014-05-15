(function (define) {
    'use strict';

    define([], function () {

        /**
         * @constructor
         */
        var DashboardCtrl = function ($scope, $log) {
            $log = $log.getInstance('DashboardCtrl');
            $log.debug('constructor() ');

            $scope.message = 'Message from DashboardCtrl';
        };

        return ['$scope', '$log', DashboardCtrl];
    });
})(define);