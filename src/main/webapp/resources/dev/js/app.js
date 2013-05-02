/* global angular, console, LandingCtrl, ProjectCtrl, ProjectsListCtrl, UsersListCtrl, UserCtrl, FileUploaderCtrl */

angular.module('irida', ['irida.services', 'irida.directives', 'ui', 'ngUpload'])
    .config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    "use strict";

    $locationProvider.hashPrefix('!');

    $routeProvider.when(
        '/', {
        templateUrl: './partials/landing.html',
        controller: LandingCtrl
    })
        .when(
        '/projects', {
        templateUrl: './partials/projects.html',
        controller: ProjectsListCtrl
    })
        .when(
        '/projects/:projectId', {
        templateUrl: './partials/project.html',
        controller: ProjectCtrl
    })
        .when(
        '/users', {
        templateUrl: './partials/users.html',
        controller: UsersListCtrl
    })
        .when(
        '/users/:username', {
        templateUrl: './partials/user.html',
        controller: UserCtrl
    })
        .when(
        '/uploader', {
        templateUrl: './partials/fileTest.html',
        controller: FileUploaderCtrl
    })
        .when(
        '/logout', {
        templateUrl: './partials/landing.html',
        controller: LandingCtrl
    })
        .otherwise({
        redirectTo: '/'
    });
}])
    .factory('MessagingService', function ($rootScope) {
    'use strict';
    var messenger = {};
    messenger.broadcast = function (msg) {
        $rootScope.$broadcast(msg);
    };

    return messenger;
});

angular.module('irida.services', ['ngResource'])
    .service('AjaxService', function ($http, $q) {
    'use strict';
    return {
        create: function (url, data) {
            var deferred = $q.defer();
            $http({
                method: 'POST',
                url: url,
                data: data,
                headers: {
                    'Content-Type': 'application/json'
                }
            })
                .success(function (data) {
                deferred.resolve(data);
            })
                .error(function (data) {
                deferred.reject(data);
            });
            return deferred.promise;
        },
        get: function (url) {
            if (url) {
                var deferred = $q.defer();

                $http.get(url)
                    .success(function (data) {
                    deferred.resolve(data);
                })
                    .error(function () {
                    deferred.reject('An error occurred while getting projects');
                });

                return deferred.promise;
            }
        },
        patch: function (url, data) {
            if (url && data) {
                var deferred = $q.defer();

                $http({
                    method: 'PATCH',
                    url: url,
                    data: data,
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                    .success(function (data) {
                    deferred.resolve(data);
                })
                    .error(function (data) {
                    deferred.reject(data);
                });
                return deferred.promise;
            }
        },
        delete: function (url) {
            if (url) {
                var deferred = $q.defer();

                $http({
                    method: 'DELETE',
                    url: url,
                    headers: {
                        'Content-Type': 'application/json'
                    }
                })
                    .success(function (data) {
                    deferred.resolve(data);
                })
                    .error(function (data, status, headers, config) {
                    console.log(data);
                    console.log(status);
                    console.log(headers);
                    console.log(config);
                    deferred.reject(status);
                });
                return deferred.promise;
            }
        }
    };
});