/*jshint unused:false, undef: false */
/**
 *  Use aysnc script loader, configure the application module (for AngularJS)
 *  and initialize the application ( which configures routing )
 *
 *  @author Josh Adam
 */

(function (head) {
    'use strict';

    head.js(
        // Pre-load these for splash-screen progress bar...

        { require: '/bower_components/requirejs/require.js', size: '82718'},
        { underscore: '/bower_components/underscore/underscore.js', size: '45489'},

        { angular: '/bower_components/angular/angular.min.js', size: '104453'},
        { ngRoute: '/bower_components/angular-route/angular-route.min.js', size: '3933'},
        { ngSanitize: '/bower_components/angular-sanitize/angular-sanitize.min.js', size: '4295'}
    )
        .ready('ALL', function () {

            require.config(
                {
                    appDir: '',
                    baseUrl: '/scripts',
                    paths: {
                        // Configure alias to full paths
                        'dashboard' : '/scripts/platform/dashboard'
                    },
                    shim: {
                        'underscore': {
                            exports: '_'
                        }
                    }
                });


            require([ 'main' ], function (app) {
                // Application has bootstrapped and started...
            });


        });


}(window.head));
