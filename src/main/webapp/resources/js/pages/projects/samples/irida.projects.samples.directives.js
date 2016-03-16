(function(ng, $, page) {
  "use strict";

  function projectsSelect2 ($timeout) {
    return {
      restrict: "A",
      require: "ngModel",
      link: function(scope, elem, attrs, ctrl) {
        $(elem).select2({
          minimumLength: 2,
          ajax         : {
            url: page.urls.projects.available,
            dataType   : 'json',
            quietMillis: 250,
            data       : function (search, page) {
              return {
                term    : search, // search term
                page    : page - 1, //zero based method
                pageSize: 10
              };
            },
            results    : function (data, page) {
              var results = [];

              var more = (page * 10) < data.total;

              _.forEach(data.projects, function (p) {
                if ($rootScope.projectId !== parseInt(p.identifier)) {
                  results.push({
                    id  : p.identifier,
                    text: p.text || p.name
                  });
                }
              });

              return {results: results, more: more};
            }
          }
        });
      }
    };
  }

  ng.module("irida.projects.samples.directives", ["irida.projects.samples.service"])
    .directive("projectsSelect2", ["$timeout", projectsSelect2]);
}(window.angular, window.jQuery, window.PAGE));