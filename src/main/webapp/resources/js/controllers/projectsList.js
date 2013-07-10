/*global angular, NGS */
/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    9:51 AM
 * License: MIT
 */

(function (ng, app) {
    'use strict';

    /**
     * AngularJS Controller for the Projects List Sidebare
     *
     * @method ProjectsListCtrl
     * @param {Object} $scope
     * @param {Object} ajaxService
     * @param {Object} $location
     */
    app.controller('ProjectsListCtrl', ['$scope', 'ajaxService', '$location', function ($scope, ajaxService, $location) {

        var active = '';

        ajaxService.get('/api/projects/all').then(function(data){
           $scope.projects =data.resource.resources;
        });

        /**
         * Creates a new empty project
         *
         * @method $scope.createProject
         * @param {String} name of the project to be created
         * @return {Boolean} True if the project was created
         */
        $scope.createProject = function (name) {
            // TODO: (Josh: 2013-06-14) Create modal window to facilitate new project.
        };

        /**
         * Loads the selected project into the ui-view section.
         *
         * @method $scope.gotoProject
         * @param {Object} e - the event that triggered the call
         * @param {String} uri - the uri for the project.
         */
        $scope.gotoProject = function (e, uri) {
            if (active) {
                active.removeClass('active');
            }
            active = ng.element(e.currentTarget);
            active.addClass('active');
            $location.path(uri.match(/\/projects\/.*$/)[0] + "/samples");
        };

    }]);
})(angular, NGS);
