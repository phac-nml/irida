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
     * AngularJS Controller for the Projects List Sidebar
     *
     * @method ProjectsListCtrl
     * @param {Object} $scope
     * @param {Object} ajaxService
     * @param {Object} $location
     */
    app.controller('ProjectsListCtrl', ['$rootScope', '$scope', 'ajaxService', '$location', '$dialog', function ($rootScope, $scope, ajaxService, $location, $dialog) {

        var active = '';

        $scope.$on('PROJECT_CREATED', function (event, msg) {
           $scope.projects.unshift(msg);
            $rootScope.$broadcast('NOTIFY', {msg:msg.name + " created."});
        });

        // DOM
//        var DOM_TOP_BOTTOM = $('#sidebar-top').height();
//        var DOM_BOTTOM_TOP = $('#sidebar-bottom').offset().top;
//        var DOM_SIDEBAR_INNER = $('.sidebar-inner');

//        $document.keydown(function (e) {
//                if (e.which === 40) {
//                    e.preventDefault();
//                    if (active) {
//                        var next = active.next();
//                        if (next.length > 0) {
//                            active.removeClass('active');
//                            active = next.addClass('active');
//                            var diff = DOM_BOTTOM_TOP - (active.offset().top + active.outerHeight());
//                            if (diff < 0) {
//                                DOM_SIDEBAR_INNER.scrollTop(DOM_SIDEBAR_INNER.scrollTop() - diff);
//                            }
//                        }
//                    }
//                }
//                else if (e.which === 38) {
//                    e.preventDefault();
//                    if (active) {
//                        var prev = active.prev();
//                        if (prev.length > 0) {
//                            active.removeClass('active');
//                            active = prev.addClass('active');
//                            var diff = active.offset().top - DOM_TOP_BOTTOM;
//                            if (diff < 0) {
//                                DOM_SIDEBAR_INNER.scrollTop(DOM_SIDEBAR_INNER.scrollTop() + diff);
//                            }
//                        }
//                    }
//                }
//        });

        ajaxService.get('/api/projects/all').then(function (data) {
            $scope.projects = data.resource.resources;
        });

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
            active = $(e.currentTarget);
            active.addClass('active');
            $location.path(uri.match(/\/projects\/.*$/)[0] + "/samples");
        };

        /**
         * New Project Stuff
         */

        var t = '<div class="modal-header">' +
            '<h3>Create New Project</h3>' +
            '</div>' +
            '<div class="modal-body">' +
            '<p>Project Name: <input ng-model="project.name" /></p>' +
            '</div>' +
            '<div class="modal-footer">' +
            '<button data-ng-click="closeModal()" class="btn" >Cancel</button>' +
            '<button ng-click="createProject()" class="btn btn-primary" >Create</button>' +
            '</div>';


        var opts = {
            backdrop: true,
                keyboard: true,
                backdropClick: true,
                template: t,
                controller: 'NewProjectCtrl'
        };

        $scope.projectModal = {
            openDialog: function () {
                var d = $dialog.dialog(opts);
                d.open();
            },
        };
    }]);

    app.controller('NewProjectCtrl', function ($rootScope, $scope, dialog, ajaxService) {
        $scope.project = {
            name: ''
        };
        $scope.closeModal = function () {
            dialog.close();
            $scope.project.name = '';
        };

        $scope.createProject = function () {
            dialog.close();
            ajaxService.create('/api/projects', {name:$scope.project.name}).then(function (uri) {
                var msg = {
                    name: $scope.project.name,
                    links: {
                        self: uri
                    }
                };
                $rootScope.$broadcast('PROJECT_CREATED', msg);
            });
        };
    });
})(angular, NGS);
