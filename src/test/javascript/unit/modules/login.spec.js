describe('irida.login', function () {
    'use strict';

    beforeEach(module('irida.login'));

    describe('LoginCtrl', function () {
        var $scope;

        beforeEach(inject(function ($rootScope, $controller) {
            $scope = $rootScope.$new();
            $controller('LoginCtrl', {$scope: $scope});
        }));

        it('should not show an error by default', function () {
           expect($scope.error).toBe(false);
        });
    });
});