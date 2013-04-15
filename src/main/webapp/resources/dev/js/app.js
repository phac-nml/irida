/**
 * Author: Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:   2013-04-15
 * Time:   8:31 AM
 */

angular.module('fwsApp', ['usersServices']).
  config(['$httpProvider', function($httpProvider) {
    "use strict";
    $httpProvider.defaults.headers.get = {'Content-Type': 'application/json'};
  }]);