/*globals define:true */
define([], function () {
    'use strict';

    return {
        defaultRoutePath: '/dashboard',
        routes: {
            '/dashboard': {
                templateUrl: '/dashboard/view/main',
                dependencies: [
                    'controllers/DashboardCtrl'
                ]
            },
            '/projects': {
                templateUrl: '/projects/view/main',
                dependencies: [
                    'controllers/ProjectsCtrl'
                ]
            }
        }
    };
});