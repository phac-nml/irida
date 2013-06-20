angular.module('filters', []).
  filter('angularMoment', function () {
    "use strict";
    return  function (date) {
      return moment(date).calendar();
      }
  });