define(['angularAMD', 'angular-route'], function (angularAMD) {
    var app = angular.module('webapp', ['ngRoute']);

    app.config(function ($routeProvider) {
        $routeProvider
            .when('/dashboard', angularAMD.route({
                templateUrl: '/dashboard/partial',
                controller: 'DashboardCtrl',
                controllerUrl: '/dashboard/controller'
            }))
            .otherwise({redirectTo: '/dashboard'});
    });

    angularAMD.bootstrap(app);

    return app;
});