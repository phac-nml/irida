module.exports = function ($scope, $location) {
    'use strict';

    $scope.error = false;
    if ($location.absUrl().search('error') > -1) {
        $scope.error = true;
    }
};