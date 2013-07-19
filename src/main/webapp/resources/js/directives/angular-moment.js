angular.module('filters', []).
    filter('angularMoment', function () {
        "use strict";
        return  function (date) {
            if (date) {
                return moment(date).calendar();
            }

        }
    });
