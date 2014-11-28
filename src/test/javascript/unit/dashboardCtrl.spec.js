define(['controllers/DashboardCtrl'], function () {
    'use strict';

    describe('The "DashboardCtrl"', function () {
        var $rootScope;
        var $controller;
        var $scope;

        beforeEach(function () {
            module('app');

            inject
            ([
                '$injector',
                '$rootScope',
                '$controller',

                function ($injector, _$rootScope, _$controller) {
                    $rootScope = _$rootScope;
                    $scope = $rootScope.$new();
                    $controller = _$controller;
                }
            ]);

            $controller('DashboardCtrl as ctrl', {$scope: $scope});
        });

        it('should display a welcome message', function () {
            expect($scope.ctrl.message).toBe('Message from DashboardCtrl');
        });
    });
});