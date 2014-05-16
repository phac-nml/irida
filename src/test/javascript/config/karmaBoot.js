(function (angular, requirejs) {
    'use strict';

    var tests = [];

    for (var file in window.__karma__.files) {
        if (/\.spec\.js$/.test(file)) {
            tests.push(file);
        }
    }

    requirejs.config({

        // Karma serves files from '/base'

        baseUrl: '/base/src/main/webapp/',

        paths: {

            'underscore': 'bower_components/underscore/underscore-min',

            'angular': 'bower_components/angular/angular',
            'ngRoute': 'bower_components/angular-route/angular-route',
            'ngSanitize': 'bower_components/angular-sanitize/angular-sanitize',
            'mocks': 'bower_components/angular-mocks/angular-mocks',

            // Configure alias to full paths

            'dashboard': 'scripts/platform/dashboard',
            'utils': 'scripts/utils'
        },

        shim: {
            'angular': {
                exports: 'angular'
            },
            'underscore': {
                exports: '_'
            }
        },

        priority: [ 'angular' ],

        // ask Require.js to load these test *Spec.js files
        deps: tests,

        // auto start test runner, once Require.js is done
        callback: window.__karma__.start
    });

    var dependencies = [
        'angular',
        'utils/logger/ExternalLogger',
        'utils/logger/LogDecorator'
    ];

    /**
     * Register the  class with RequireJS
     *
     * Notice: the dependencies are NOT used as arguments
     */
    require(dependencies, function (angular) {
        var appName = 'test.irida';

        angular.module(appName, [ ]);
    });


})(angular, requirejs);
