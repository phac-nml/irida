(function (define) {
    'use strict';

    define([], function () {

        /**
         * @constructor
         */
        var DashboardCtrl = function ($scope) {

            $scope.message = 'Message from DashboardCtrl';
        };

        return ['$scope', DashboardCtrl];
    });
})(define);