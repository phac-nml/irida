/* =========================================================================
 AUTHOR:   Josh Adam <josh.adam@phac-aspc.gc.ca>
 DATE:     4 June, 2013
 COMMENTS: Runs all the angular scripts for the project.
 LICENSE:  MIT
 ========================================================================= */
var NGS = angular.module('NGS', [
  'ngCookies',
  'http-auth-interceptor',
  'ngResource',
  'ui.state',
  'ngs-section'
]);

/**
 * Allows for the use of URLs without the !# in it
 */
NGS.config(['$locationProvider', function ($locationProvider) {
  'use strict';
  $locationProvider.html5Mode(true);
}]);

//NGS.config(['$routeProvider', function ($routeProvider) {
//  'use strict';
//
//  $routeProvider.when('/projects', {
//    templateUrl: '/partials/landing.html',
//    reloadOnSearch: false,
//    controller: function ($scope, data) {
//      $scope.projects = data.resource.resources;
//    },
//    resolve: {
//      data: function ($q, ajaxService) {
//        var defer = $q.defer();
//        ajaxService.get('/api/projects').then(function (data) {
//          defer.resolve(data);
//        });
//        return defer.promise;
//      }
//    }
//  });

//  $routeProvider.when('/projects/{{id}}', {
//    templateUrl: '/partials/landing.html',
//    reloadOnSearch: false,
//    controller: function ($scope, data) {
//      $scope.projects = data.resource.resources;
//    },
//    resolve: {
//      data: function ($q, ajaxService) {
//        var defer = $q.defer();
//        ajaxService.get('/api/projects').then(function (data) {
//          defer.resolve(data);
//        });
//        return defer.promise;
//      }
//    }
//  });

//  $routeProvider.when('/', {
//    templateUrl: '/partials/landing.html',
//    controller: 'LandingCtrl'
//  });

//  $routeProvider.otherwise({redirectTo: '/'});
//}]);

NGS.run(['$cookieStore', '$http', function ($cookieStore, $http) {
  'use strict';
  var cookie = $cookieStore.get('authdata');
  if (cookie) {
    $http.defaults.headers.common.Authorization = 'Basic ' + $cookieStore.get('authdata');
  }
}]);