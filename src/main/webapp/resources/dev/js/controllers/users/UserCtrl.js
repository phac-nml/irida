/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date: 2013-04-17
 * Time: 9:41 AM
 */

var irida = angular.module('irida');


function UserCtrl($scope, $window, AjaxService) {
    'use strict';

    $scope.user = {};
    $scope.links = {};
    $scope.projects = [];

    $scope.init = function() {
        var username = /\/users\/(.*)$/.exec($window.location.pathname)[1];

        AjaxService.get('/users/' + username).then(

        function(data) {
            initialAjaxCallback(data);
        },

        function(errorMessage) {
            // TODO: handle error message
        });
    };

    function initialAjaxCallback(data) {
        "use strict";
        angular.forEach(data.resource.links, function(val) {
            $scope.links[val.rel] = val.href;
        });
        delete data.resource.links;
        $scope.user = data.resource;
        getUserProjects();
    }

    function getUserProjects() {
        AjaxService.get($scope.links['user/projects']).then(

        function(data) {
            $scope.projects = data.projectResources.projects;

        },

        function(errorMessage) {
            // TODO: handle error message
        });
    }
}
irida.controller(UserCtrl);