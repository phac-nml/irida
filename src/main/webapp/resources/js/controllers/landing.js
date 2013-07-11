/**
 * Created by josh on 2013-06-20.
 */
(function (ng, app) {
    'use strict';

    app
        .controller('LandingCtrl', ['$scope', 'ajaxService', function ($scope, ajaxService) {
            ajaxService.get('/api/users/current').then(function (data) {
                console.log(data);
                $scope.user = data.resource;
            });
        }]);
})(angular, NGS);
