(function () {
  "use strict";
  function PhylogenomicsController($http) {
    var vm = this;

    vm.launch = function () {
      var ref = Number(angular.element('option:selected').val()),
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

      $http.post(PIPELINE.url, {ref: ref, single: single, paired: paired});
    };
  }

  angular.module('irida.pipelines.phylogenomics', [])
    .controller('PhylogenomicsController', ['$http', PhylogenomicsController])
  ;
})();