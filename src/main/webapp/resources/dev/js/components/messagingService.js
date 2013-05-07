/**
 * Created with JetBrains WebStorm.
 * User: josh
 * Date: 2013-05-06
 * Time: 9:01 PM
 * To change this template use File | Settings | File Templates.
 */

angular.module('messagingService', [])
  .factory('messagingService', ['$rootScope', function ($rootScope) {
    'use strict';
    var messenger = {};
    messenger.broadcast = function (msg) {
      $rootScope.$broadcast(msg);
    };

    return messenger;
  }]);