(function (define) {
    'use strict';
    define(['app'], function (app) {
        app.controller('DashboardCtrl', ['$scope', function ($scope) {
            $scope.message = 'Message from DashboardCtrl';
        }]);
    });
})(define);