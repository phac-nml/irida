(function(ng, $) {
  "use strict";

  function projectsSelect2 ($timeout) {
    return {
      restrict: "A",
      require: "ngModel",
      link: function(scope, elem, attrs, ctrl) {
        $(elem).select2({

        });
      }
    };
  }

  ng.module("irida.projects.samples.directives", ["irida.projects.samples.service"])
    .directive("projectsSelect2", ["$timeout", projectsSelect2]);
}(window.angular, window.jQuery));