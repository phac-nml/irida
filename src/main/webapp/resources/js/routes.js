/*globals define:true */
define([], function () {
    'use strict';
    var baseUrl = '/',
        baseElm = document.getElementById('baseUrl');

    if(baseElm !== null) {
        baseUrl = baseElm.value;
    }

    return {
        defaultRoutePath: '/dashboard',
        routes: {
            '/dashboard': {
                templateUrl: baseUrl + 'dashboard/view/main',
                dependencies: [
                    'controllers/DashboardCtrl'
                ]
            },
            '/projects': {
                templateUrl: baseUrl + 'projects/view/main',
                dependencies: [
                    'controllers/ProjectsCtrl'
                ]
            }
        }
    };
});