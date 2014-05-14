define(['angularAMD', 'angular-route'], function (angularAMD) {
    var app = angular.module('webapp', ['ngRoute']);

    app.config(function ($routeProvider) {
        $routeProvider
            .when('/dashboard', angularAMD.route({
                templateUrl: '/view/dashboard',
                controller: 'DashboardCtrl',
                controllerUrl: '/scripts/dashboard/controllers/dashboardCtrl.js'
            }))
            .otherwise({redirectTo: '/dashboard'});
    });

    angularAMD.bootstrap(app);

    return app;
});