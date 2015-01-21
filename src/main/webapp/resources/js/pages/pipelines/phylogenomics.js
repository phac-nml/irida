(function () {
  "use strict";
  function PhylogenomicsController($http, notifications) {
    var vm = this;

    vm.loading = false;

    vm.launch = function () {
      vm.loading = true;
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

      $http.post(PIPELINE.url, {ref: ref, single: single, paired: paired, name: name})
        .success(function (data, status, headers, config) {
            vm.loading = false;
          if(data.result === 'success') {
            // TODO: Redirect to the pipelines page showing the current status of all pipelines.
            notifications.show({msg: "TODO: Redirect to running pipelines page."})
          }
          else {
            // TODO (Josh - 15-01-20) Display error message to the user.
          }
        })
        .error(function (data, status, headers, config) {

        });
    };
  }

  angular.module('irida.pipelines.phylogenomics', [])
    .controller('PhylogenomicsController', ['$http', 'notifications', PhylogenomicsController])
  ;
})();