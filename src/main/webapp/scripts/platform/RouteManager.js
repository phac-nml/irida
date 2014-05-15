/**
 * ******************************************************************************************************
 *
 *   RouteManager
 *
 *   Defines routes used in the IRIDA Web Platform
 *
 *  @author     Josh Adam
 *
 * ******************************************************************************************************
 */

(function (define) {
    'use strict';


    define([
            'utils/logger/ExternalLogger',
            'dashboard/controllers/DashboardCtrl'
        ],
        function ($log, DashboardCtrl) {
            /**
             * Route management constructor ()
             * - to be used in angular.config()
             *
             * @see bootstrap.js
             */
            var RouteManager = function ($routeProvider) {
                $log.debug('Configuring $routeProvider...');

                $routeProvider
                    .when('/dashboard', {
                        templateUrl: '/view/dashboard',
                        controller: 'DashboardCtrl'
                    })
                    .otherwise({
                        redirectTo: '/dashboard'
                    });

            };

            $log = $log.getInstance('RouteManager');

            return ['$routeProvider', RouteManager ];
        });


}(define));
