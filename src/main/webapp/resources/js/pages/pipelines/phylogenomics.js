(function () {
  "use strict";
  function PhylogenomicsController($http) {
    var vm = this;

    vm.launch = function () {
      var ref = Number(angular.element('option:selected').val()),
          name = angular.element('#pipeline-name').val(),
          radioBtns = angular.element("input[type='radio']:checked"),
          single = [],
          paired = [];

      _.forEach(radioBtns, function (c) {
        c = $(c);
        if(c.attr('data-type') === 'single_end'){
          single.push(Number(c.val()));
        }
        else {
          paired.push(Number(c.val()));
        }
      });

      $http.post(PIPELINE.url, {ref: ref, single: single, paired: paired, name: name});
    };
  }

  angular.module('irida.pipelines.phylogenomics', [])
    .controller('PhylogenomicsController', ['$http', PhylogenomicsController])
  ;
})();