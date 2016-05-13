(function (ng, page) {
  "use strict";

  var SampleService = (function () {
    var post;
    function SampleService ($http) {
      post = $http.post
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

    return SampleService;
  }());

  ng.module("irida.projects.samples.service", ["irida.cart"])
    .service('SampleService', ["$http", SampleService])
  ;
}(window.angular, window.PAGE));