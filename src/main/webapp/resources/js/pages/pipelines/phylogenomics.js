(function () {
  "use strict";
  /**
   * Main controller for the phylogenomics pipeline page.
   * @param $http AngularJS http object
   * @constructor
   */
  function PhylogenomicsController($http) {
    var vm = this;
    /*
     * Whether or not the page is waiting for a response from the server.
     */
    vm.loading = false;

    /**
     * Launch the phylogenomics pipeline
     */
    vm.launch = function () {
      var
      // reference file id
      ref = Number(angular.element('option:selected').val()),
      // User defined name for the pipeline
      name = angular.element('#pipeline-name').val(),
      // All the selected sample single or pair-end files
      radioBtns = angular.element("input[type='radio']:checked"),
      // Holds all the ids for the selected single-end
      single = [],
      // Holds all the ids for the selected paired-end
      paired = [];

      if (name === null || name.length === 0) {
        vm.error = PIPELINE.required;
      } else {
        vm.loading = true;
        _.forEach(radioBtns, function (c) {
          c = $(c);
          if (c.attr('data-type') === 'single_end') {
            single.push(Number(c.val()));
          }
          else {
            paired.push(Number(c.val()));
          }
        });

        $http.post(PIPELINE.url, {ref: ref, single: single, paired: paired, name: name})
          .success(function (data, status, headers, config) {
            if (data.result === 'success') {
              vm.success = true;
            }
            else {
              vm.error = data.error;
            }
          })
          .error(function (data, status, headers, config) {

          });
      }
    };
  }

  angular.module('irida.pipelines.phylogenomics', [])
    .controller('PhylogenomicsController', ['$http', PhylogenomicsController])
  ;
})();