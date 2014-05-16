/**
 * ******************************************************************************************************
 *
 *   IridaModule
 *
 *   Defines controllers and services for the IRIDA Web Platform
 *
 *  @author     Josh Adam
 *
 * ******************************************************************************************************
 */

(function (define, angular) {
    'use strict';

    define([
            'dashboard/controllers/DashboardCtrl'
        ],
        function (DashboardCtrl) {
            var moduleName = 'irida.main';

            angular.module(moduleName, [ ])
                .controller('DashboardCtrl', DashboardCtrl);

            return moduleName;
        });

}(define, angular));

