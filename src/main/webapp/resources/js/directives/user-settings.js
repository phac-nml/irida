angular.module('user-settings', []).directive('userSettings', function () {
    'use strict';
    return {
        restrict: 'A',
        templateUrl: '/partials/user-settings.html',
        link: function (scope, elem, attrs) {
//            ajaxService.get('/api/users/')
        }
    };
});
