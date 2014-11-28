define(['controllers/ProjectsCtrl'], function () {
    'use strict';

    describe('The "ProjectsCtrl"', function () {
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

            $controller('ProjectsCtrl as ctrl', {$scope: $scope});
        });

        it('should display a welcome message', function () {
            expect($scope.ctrl.message).toBe('Message from ProjectsCtrl');
        });
    });
});