(function (ng, $, page) {
  "use strict";

  var SampleService = (function () {
    var post, get;
    var location;
    var scope;

    function _clearFileFilteredURL() {
      var url = oTable_samplesTable.ajax.url(),
          index = url.indexOf("?");
      if(index > -1) {
        url = url.substring(0, url.indexOf("?"));
      }
      oTable_samplesTable.ajax.url(url);
    }

    function _reloadTable() {
      _clearFileFilteredURL();
      page.ajaxParam = {date:{}};
      oTable_samplesTable.search("");
      oTable_samplesTable.ajax.reload();
    }

    function SampleService ($http, $window, $rootScope) {
      post = $http.post;
      get = $http.get;
      location = $window.location;
      scope = $rootScope;

      /**
       * Clear specific filters applied to the samples table.
       */
      scope.$on('FILTER_CLEARED', function (event, args) {
        if (args.type === "file") {
          _clearFileFilteredURL();
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
      scope.$on("FILTER_TABLE", function (event, args) {
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

    function copyMoveSamples(params) {
      return post(page.urls.samples.copy, params)
          .success(function (result) {
            showCopyRemoveErrors(result);
          });
    }

    SampleService.prototype.merge = function (data) {
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

    SampleService.prototype.copy = function (params) {
      return copyMoveSamples(params);
    };

    SampleService.prototype.move = function (params) {
      params.remove = true;
      return copyMoveSamples(params);
    };

    SampleService.prototype.remove = function (ids) {
      return post(page.urls.samples.remove, {sampleIds: ids})
        .success(function (data) {
          notifications.show({type: data.result, msg: data.message})
        });
    };

    SampleService.prototype.download = function (ids) {
      var url = page.urls.samples.download + "?" + $.param({ids: ids});
      var iframe = document.querySelector("#download-iframe");
      if (iframe === null) {
        iframe = document.createElement("iframe");
        iframe.setAttribute("id", "download-iframe");
        iframe.style.display = "none";
      }
      iframe.setAttribute('src', url);
      document.body.appendChild(iframe);
    };

    SampleService.prototype.ncbiExport = function (ids) {
      location.href = page.urls.samples.ncbi + "?" + $.param({ids: ids});
    };

    SampleService.prototype.filterBySampleNames = function(sampleNames) {
      oTable_samplesTable.ajax.url(page.urls.samples.project + "/?" + $.param({sampleNames: sampleNames})).load(function (result) {
        var difference = sampleNames.length - result.recordsTotal;
        if (difference === 0) {
          notifications.show({type: "success", msg: page.i18n.fileFilter.success});
        } else {
          post(page.urls.fileMissingSamples, {sampleNames: sampleNames}).then(function (response) {
            var msg = "<strong>" + response.data.message + "</strong><ul>";
            response.data.missingNames.forEach(function(name) {
              msg += "<li>" + name + "</li>"
            });
            msg += "</ul>";
            notifications.show({type: "warning", msg: msg, timeout: false});
          });
        }
        scope.$broadcast("FILE_FILTER");
      });
    };

    SampleService.prototype.exportToFile = function (type) {
      function getQueryParamsObject(paramString) {
        var searchParams = new URLSearchParams(paramString);
        return ({
          sampleNames: searchParams.getAll("sampleNames[]")
        });
      }

      var requestString = oTable_samplesTable.ajax.url().split('?')[1];
      var params = getQueryParamsObject(requestString);
      params.type = type;
      var tableParams = oTable_samplesTable.ajax.params();
      ng.extend(params, tableParams);
      post(page.urls.samples.export, params);
    };

    return SampleService;
  }());

  ng.module("irida.projects.samples.service", ["irida.cart"])
    .service('SampleService', ["$http", "$window", "$rootScope", SampleService])
  ;
}(window.angular, window.jQuery, window.PAGE));
