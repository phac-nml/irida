(function (ng, $, page, project) {
  "use strict";

  function _generateProjectsSelect2Results(projects) {
    return projects.filter(function (p) {
      return p.identifier != project.id;
    }).map(function (p) {
      return ({
        id  : p.identifier,
        text: p.text || p.name
      });
    });
  }

  function projectsSelect2 () {
    return {
      restrict: "A",
      require: "ngModel",
      link: function(scope, elem) {
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
              var more    = (page * 10) < data.total,
                  results = _generateProjectsSelect2Results(data.projects);
              return {results: results, more: more};
            }
          }
        });
      }
    };
  }

  ng.module("irida.projects.samples.directives", ["irida.projects.samples.service"])
    .directive("projectsSelect2", [projectsSelect2]);
}(window.angular, window.jQuery, window.PAGE, window.project));