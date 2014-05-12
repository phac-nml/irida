describe('irida.projects', function () {
    'use strict';

    beforeEach(module('irida.projects'));

    describe('ProjectsMgrCtrl', function () {
        var $scope;

        beforeEach(inject(function ($rootScope, $controller) {
            $scope = $rootScope.$new();
            $controller('ProjectsMgrCtrl', {$scope: $scope});
        }));

        it('should have a title', function () {
            expect($scope.data.title).toBe("Right Said Fred!");
        });
    });
});