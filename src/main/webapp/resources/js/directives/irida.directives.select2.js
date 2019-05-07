(function(ng, $) {
  "use strict";

  function select2() {
    return {
      restrict: "A",
      require: "ngModel",
      scope: {
        url: "@",
        select2Fn: "&"
      },
      link: function(scope, elem) {
        $(elem).select2({
          minimumLength: 2,
          ajax: {
            url: scope.url,
            dataType: "json",
            quietMillis: 250,
            data: function(search, page) {
              return {
                term: search, // search term
                page: page - 1, //zero based method
                pageSize: 10
              };
            },
            results: function(data, page) {
              var more = page * 10 < data.total,
                results = scope.select2Fn()(data);
              return { results: results, more: more };
            }
          }
        });
      }
    };
  }

  ng.module("irida.directives.select2", []).directive("select2", [select2]);
})(window.angular, window.jQuery);
