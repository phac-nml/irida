/* global angular */
var irida = angular.module('irida');

function MainCtrl($scope, MessagingService) {
    "use strict";

    $scope.notifier = {
        message: '' ,
        icon: ''
    };
}
irida.controller(MainCtrl);