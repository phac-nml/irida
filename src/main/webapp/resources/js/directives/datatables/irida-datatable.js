(function(angular) {
  angular.module("irida.datatables", []).directive("sortBy", function() {
    "use strict";
    return {
      template:
        '<a class="clickable" ng-click="sort(sortValue)">' +
        '<span ng-transclude=""></span>' +
        '<span class="pull-right" ng-show="sortedby == sortvalue">' +
        "<i class=\"fa fa-fw\" ng-class=\"{true: 'fa-sort-asc', false: 'fa-sort-desc'}[sortdir == 'asc']\"></i>" +
        '</span><span class="pull-right" ng-show="sortedby != sortvalue"><i class="fa fa-sort fa-fw"></i></span></a>',
      restrict: "EA",
      transclude: true,
      replace: false,
      scope: {
        sortdir: "=",
        sortedby: "=",
        sortvalue: "@",
        onsort: "="
      },
      link: function(scope) {
        scope.sort = function() {
          if (scope.sortedby === scope.sortvalue) {
            scope.sortdir = scope.sortdir === "asc" ? "desc" : "asc";
          } else {
            scope.sortedby = scope.sortvalue;
            scope.sortdir = "asc";
          }
          scope.onsort(scope.sortedby, scope.sortdir);
        };
      }
    };
  });
})(window.angular);
