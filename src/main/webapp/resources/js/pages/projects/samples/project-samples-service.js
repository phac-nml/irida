(function(ng, $, page) {
  "use strict";

  /**
   * Angular service for handling server interactions with samples.
   */
  var SampleService = (function() {
    var post, get;
    var location;
    var scope;

    /**
     * Internal function to reload the datatable.
     * @private
     */
    function _reloadTable() {
      // Need to keep the associated projects
      page.ajaxParam = {date: {}, associated: page.ajaxParam.associated};
      // Clear the search field if needed.
      oTable_samplesTable.search("");
      oTable_samplesTable.ajax.reload();
    }

    /**
     * Get the current state of the datatable filters.
     * @returns {{}}
     * @private
     */
    function _getFilterState() {
      var filters = {};
      Object.assign(filters, page.ajaxParam);
      var searchTerm = document.querySelector(".dataTables_filter input").value;
      filters.search = searchTerm;
      return filters;
    }

    function SampleService($http, $window, $rootScope) {
      post = $http.post;
      get = $http.get;
      location = $window.location;
      scope = $rootScope;

      /**
       * Clear specific filters applied to the samples table.
       */
      scope.$on('FILTER_CLEARED', function(event, args) {
        if (args.type === "file") {
          delete page.ajaxParam.sampleNames;
        } else {
          delete page.ajaxParam[args.type]
        }
        oTable_samplesTable.ajax.reload();
      });

      /**
       * Clear all filters that are active on the project samples table.
       */
      scope.$on("CLEAR_FILTERS", _reloadTable);

      /**
       * Filter the project samples table based on the filter modal.
       */
      scope.$on("FILTER_TABLE", function(event, args) {
        // Setting the page object, since dandelion datatables establishes the custom
        // params dynamically.  We add to them in the sample-ajax-params.js file.
        ng.extend(page.ajaxParam, args.filter);
        // Reload the tables (which will call for these custom params).
        oTable_samplesTable.ajax.reload();
      });
    }

    /**
     * Show all success and error messages for samples being copied.
     * @param result
     */
    function showCopyRemoveErrors(result) {
      if (result.message) {
        notifications.show({type: "success", msg: result.message});
      }
      if (result.warnings) {
        result.warnings.forEach(function(warning) {
          notifications.show({type: "warning", msg: warning});
        })
      }
    }

    /**
     * Copy or move the samples.
     * @param params
     * @returns {*}
     * @private
     */
    function _copyMoveSamples(params) {
      return post(page.urls.samples.copy, params)
        .success(function(result) {
          showCopyRemoveErrors(result);
        });
    }

    /**
     * Merge samples
     * @param {object} data of sample ids and where to merge them.
     * @returns {*}
     */
    SampleService.prototype.merge = function(data) {
      var params = {
        sampleIds: data.ids,
        mergeSampleId: data.mergeSampleId,
        newName: data.newName
      };
      return post(page.urls.samples.merge, params)
        .success(function(result) {
          notifications.show({type: result.result, msg: result.message});
        });
    };

    /**
     * Copy samples to another project
     * @param params
     * @returns {*}
     */
    SampleService.prototype.copy = function(params) {
      return _copyMoveSamples(params);
    };

    /**
     * Move samples to another project
     * @param params
     * @returns {*}
     */
    SampleService.prototype.move = function(params) {
      params.remove = true;
      return _copyMoveSamples(params);
    };

    /**
     * Remove samples from this project.
     * @param ids
     * @returns {*}
     */
    SampleService.prototype.remove = function(ids) {
      return post(page.urls.samples.remove, {sampleIds: ids})
        .success(function(data) {
          notifications.show({type: data.result, msg: data.message})
        });
    };

    /**
     * Download a samples by sample id.
     * @param ids
     */
    SampleService.prototype.download = function(ids) {
      var url = page.urls.samples.download + "?" + $.param({ids: ids});
      // Need an iframe so it does not load into the current window.
      var iframe = document.querySelector("#download-iframe");
      if (iframe === null) {
        iframe = document.createElement("iframe");
        iframe.setAttribute("id", "download-iframe");
        iframe.style.display = "none";
      }
      iframe.setAttribute('src', url);
      document.body.appendChild(iframe);
    };

    /**
     * Load the NCBI export page based on selected samples.
     * @param ids
     */
    SampleService.prototype.ncbiExport = function(ids) {
      location.href = page.urls.samples.ncbi + "?" + $.param({ids: ids});
    };

    /**
     * Filter the datatable based on a set of sample names.
     * @param {array} sampleNames
     */
    SampleService.prototype.filterBySampleNames = function(sampleNames) {
      // Store the sample names so datatables can add them to the url
      // This is done in sample-ajax-params.js
      page.ajaxParam.sampleNames = sampleNames;
      oTable_samplesTable.ajax.reload(function() {
        // Need to see if anything is missing
        post(page.urls.fileMissingSamples, {
          sampleNames: sampleNames,
          projects: page.ajaxParam.associated
        }).then(function(response) {
          if (response.data.missingNames) {
            var msg = "<strong>" + response.data.message + "</strong><ul>";
            response.data.missingNames.forEach(function(name) {
              msg += "<li>" + name + "</li>"
            });
            msg += "</ul>";
            notifications.show({type: "warning", msg: msg, timeout: false});
          } else {
            notifications.show({type: "success", msg: response.data.success})
          }
        });
        scope.$broadcast("FILE_FILTER");
      });
    };

    /**
     * Export the datatable to a file.
     * @param {string} type either 'xlsx' or 'csv'
     */
    SampleService.prototype.exportToFile = function(type) {
      // Need the parameters from the datatable so that we know the correct filters.
      var tableParams = oTable_samplesTable.ajax.params();
      tableParams.dtf = type; // This is an important parameter name for exporting the correct format.

      // Need an iframe here since it is a document download not just an ajax request.
      var iframe = document.createElement("iframe");
      iframe.style.display = "none";
      iframe.src = page.urls.samples.export + "?" + $.param(tableParams);
      document.body.appendChild(iframe);
    };

    /**
     * Update the table to display any associated projects.
     * @param {array} projectsIds - list of project ids for associated projects.
     */
    SampleService.prototype.updateAssociatedProjects = function(projectsIds) {
      page.ajaxParam.associated = projectsIds;
      oTable_samplesTable.ajax.reload();
    };

    /**
     * Get all the ids for the samples currently displayed in the datatable.
     * @returns {*}
     */
    SampleService.prototype.getAllIds = function() {
      return get(page.urls.samples.sampleIds + "?" + $.param(_getFilterState()))
        .success(function(result) {
          return result;
        });
    };

    return SampleService;
  }());

  ng.module("irida.projects.samples.service", ["irida.cart"])
    .service('SampleService', ["$http", "$window", "$rootScope", SampleService])
  ;
}(window.angular, window.jQuery, window.PAGE));
