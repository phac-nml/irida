/**
 * Created by josh on 2013-07-16.
 */

(function (ng, app) {
    'use strict';
    app.controller('ProjectUsersCtrl', [ '$scope', 'ajaxService', 'projectService',
        function ($scope, ajaxService, projectService) {
            $scope.data = {
                users: []
            };

            ajaxService.get('/users').then(function (data) {
                ng.forEach(data.resource.resources, function (u) {
                    if (!userInProject(u.identifier)) {
                        $scope.data.users.push(u.firstName + ' ' + u.lastName + ' (' + u.username + ')');
                    }
                });
            });

            var list = projectService.project.users;
            function userInProject(id) {
                var i;
                for (i = 0; i < list.length; i++) {
                    if (list[i].identifier === id) {
                        return true;
                    }
                }

                return false;
            }

            $scope.addUserToProject = function($event) {
                if($scope.data.users.indexOf($scope.data.newUser) != -1) {
                    var id = $scope.data.newUser.match(/\((.*)\)/)[1];
                    ajaxService.create(projectService.project.links['project/users'], {userId: id});
                }
            };
        }
    ]);
})(angular, NGS);
