/*global angular, NGS */
/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-04
 * Time:    9:11 AM
 * License: MIT
 */
(function (ng, app) {
    'use strict';
    app.config(function ($stateProvider) {
        $stateProvider
        /**
         * Landing Page
         */
            .state('projects', {
                url: '/',
                templateUrl: '/partials/landing.html'
//                resolve: {
//                    data: function ($q, ajaxService) {
//                        var defer = $q.defer();
//                        ajaxService.get('/api/projects/all').then(function (data) {
//                            defer.resolve(data);
//                        });
//                        return defer.promise;
//                    }
//                },
//                controller: function ($scope, data) {
//                    $scope.projects = data.resource.resources;
//                }
            })
            .state('projects.main', {
                url: 'landing',
                templateUrl: '/partials/default-landing.html'
            })
        /**
         * Project Page
         */
            .state('projects.detail', {
                url: 'projects/:projectId',
                templateUrl: '/partials/project.html',
                resolve: {
                    data: function ($q, $stateParams, ajaxService) {
                        var defer = $q.defer();
                        ajaxService.get('/api/projects/' + $stateParams.projectId).then(function (data) {
                            defer.resolve(data);
                        });
                        return defer.promise;
                    }
                },
                controller: function ($scope, $stateParams, data) {
                    $scope.project = {
                        date: data.resource.dateCreated,
                        id: $stateParams.projectId,
                        name: data.resource.name,
                        users: data.relatedResources.users.resources,
                        samples: data.relatedResources.samples.resources,
                        sequenceFiles: data.relatedResources.sequenceFiles.resources,
                        links: data.resource.links
                    };
                }
            })
            .state('projects.detail.samples', {
                url: '/samples',
                templateUrl: '/partials/projects.samples.html',
                controller: function ($scope) {
                    $scope.data.view = 'samples';
                }
            })
            .state('projects.detail.samples.details', {
                url: '/:sampleId',
                templateUrl: '/partials/sample.html',
                controller: function ($scope, $stateParams, data) {
                    console.log(data);
                    $scope.data = {
                        sample: data.resource,
                        sequenceFiles: data.relatedResources.sequenceFiles.resources
                    };
                },
                resolve: {
                    data: function ($q, $stateParams, ajaxService) {
                        var defer = $q.defer();
                        ajaxService.get('/api/projects/' + $stateParams.projectId + '/samples/' + $stateParams.sampleId).then(function (data) {
                            defer.resolve(data);
                        });
                        return defer.promise;
                    }
                }
            })
            .state('projects.detail.files', {
                url: '/files',
                templateUrl: '/partials/projects.files.html',
                controller: function ($scope) {
                    $scope.data.view = 'files';
                }
            })
            .state('projects.detail.users', {
                url: '/users',
                templateUrl: '/partials/projects.users.html',
                controller: function ($scope) {
                    $scope.data.view = 'users';
                }
            })
            .state('projects.users', {
                url: 'users/:userId',
                templateUrl: '/partials/user.html',
                controller: function ($scope, $stateParams, data) {
                    $scope.data = {
                        user: data.resource
                    };
                },
                resolve: {
                    data: function ($q, $stateParams, ajaxService) {
                        var defer = $q.defer();
                        ajaxService.get('/api/users/' + $stateParams.userId).then(function (data) {
                            defer.resolve(data);
                        });
                        return defer.promise;
                    }
                }
            })
            .state('login', {
                url: '/login',
                templateUrl: '/partials/login.html'
            });
    });
})
    (angular, NGS);
