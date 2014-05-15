(function (define, describe) {
    'use strict';

    var dependencies = [
        'dashboard/controllers/DashboardCtrl'
    ];

    define(dependencies, function (DashboardCtrl) {
        describe('Dashboard Controller', function () {
            var _scope,
                appName = 'test.irida';

            /**
             * Load the `test module`
             */
//            beforeEach(function () {
//                module(appName);
//            });

            beforeEach(inject(function ($rootScope, $injector, $controller) {
                _scope = $rootScope.$new();

                // Create instances DashboardCtrl with known scope...
                $controller(DashboardCtrl, {
                    $scope: _scope
                });

            }));

            afterEach(function () {
                _scope = null;
            });

            it('Should welcome the user', function () {
                expect(_scope.message).toBe('Message from DashboardCtrl');
            });
        });
    });
})(define, describe);
