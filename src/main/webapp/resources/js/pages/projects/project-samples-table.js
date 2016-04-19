// The contents of this file gets load via Dandelion Datatables to the end of IIFE.
/*global oTable_samplesTable */

var table = oTable_samplesTable;

// Add the toolbar to the table.
var $target = $("[ng-app]");
angular.element($target).injector().invoke(["$rootScope", "$compile", function ($rootScope, $compile) {
  var toolbar = $(".filter-row > div")[0],
    tools = $("#toolbar");
  $(toolbar).html(function () {
    return $compile(tools)($rootScope);
  });
}]);