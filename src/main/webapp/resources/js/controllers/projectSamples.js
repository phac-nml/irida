/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-07-17
 * Time: 8:00 AM
 * To change this template use File | Settings | File Templates.
 */
(function (ng, app) {
    'use strict';
    app.controller('ProjectSamplesCtrl', [ '$rootScope', '$scope', '$location', 'ajaxService', 'projectService',
        function ($rootScope, $scope, $location, ajaxService, projectService) {

            $scope.gotoSample = function ($event, uri) {
                var link = uri.match(/\/projects\/(.*)/)[0];
                $location.path(link);
            };
        }
    ]);
})(angular, NGS);