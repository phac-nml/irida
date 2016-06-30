(function (ng, $, page) {
  "use strict";

  var SampleService = (function () {
    var post;
    var location;

    function SampleService ($http, $window) {
      post = $http.post;
      location = $window.location;
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

    return SampleService;
  }());

  ng.module("irida.projects.samples.service", ["irida.cart"])
    .service('SampleService', ["$http", "$window", SampleService])
  ;
}(window.angular, window.jQuery, window.PAGE));
