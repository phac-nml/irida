(function (ng, page) {
  "use strict";

  var SampleService = (function () {
    var post;
    function SampleService ($http) {
      post = $http.post
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

    return SampleService;
  }());

  ng.module("irida.projects.samples.service", ["irida.cart"])
    .service('SampleService', ["$http", SampleService])
  ;
}(window.angular, window.PAGE));
