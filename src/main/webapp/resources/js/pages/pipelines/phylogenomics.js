(function () {
  "use strict";
  function PhylogenomicsController($http) {
    var vm = this;

    vm.launch = function () {
      var ref = Number(angular.element('option:selected').val()),
          cbs = angular.element("input[type='checkbox']:checked"),
          files = [];

      _.forEach(cbs, function (c) {
        c = $(c);
        files.push(Number(c.val()));
      });

      $http.post("/pipelines/ajax/phylogenomics/start", {ref: ref, files: files});
    };
  }

  angular.module('irida.pipelines.phylogenomics', [])
    .controller('PhylogenomicsController', ['$http', PhylogenomicsController])
  ;
})();