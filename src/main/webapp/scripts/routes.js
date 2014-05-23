/*globals define:true */
define([], function () {
    'use strict';
    var baseUrl = document.getElementById('baseUrl').value;

    return {
        defaultRoutePath: '/dashboard',
        routes: {
            '/dashboard': {
                templateUrl: baseUrl + '/dashboard/view/main',
                dependencies: [
                    'controllers/DashboardCtrl'
                ]
            },
            '/projects': {
                templateUrl: baseUrl + '/projects/view/main',
                dependencies: [
                    'controllers/ProjectsCtrl'
                ]
            }
        }
    };
});