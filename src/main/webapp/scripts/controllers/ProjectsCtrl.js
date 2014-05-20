(function (define) {
    'use strict';
    define(['app'], function (app) {
        app.controller('ProjectsCtrl', ['$scope', function ($scope) {
            $scope.message = 'Message from ProjectsCtrl';
        }]);
    });
})(define);