/**
 * Created with IntelliJ IDEA.
 * User: josh
 * Date: 2013-05-15
 * Time: 10:27 AM
 * To change this template use File | Settings | File Templates.
 */
angular.module('irida')
  .controller('MainCtrl', ['$scope', '$location', 'CookieService', function ($scope, $location, CookieService) {
    'use strict';
    $scope.notifier = {};
  }]);