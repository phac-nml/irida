define(['app'], function (app) {
    app.register.controller('DashboardCtrl', ['$scope', function ($scope) {
        $scope.message = 'Message from DashboardCtrl';
    }]);
});