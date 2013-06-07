/**
 * User:    Josh Adam <josh.adam@phac-aspc.gc.ca>
 * Date:    2013-06-07
 * Time:    12:12 PM
 * License: MIT
 */
(function (ng, app) {
  'use strict';
  app.factory('linkFormatter', [function () {
    return {
      clean: function (linkArray) {
        var links = {};
        angular.forEach(linkArray, function(value) {
          links[value.rel] = value.href;
        });
        return links;
      }
    };
  }]);
})(angular, NGS);