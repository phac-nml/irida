(function () {
  "use strict";
  /**
   * Main controller for the pipeline launch page.
   * @param $http AngularJS http object
   * @constructor
   */
  function PipelineController($http) {
    var vm = this;
    /*
     * Whether or not the page is waiting for a response from the server.
     */
    vm.loading = false;

    /**
     * Launch the pipeline
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
        // Hide the launch buttons and display a message that it has been sent.
        vm.loading = true;

        // Get a list of paired and single end files to run.
        _.forEach(radioBtns, function (c) {
          c = $(c);
          if (c.attr('data-type') === 'single_end') {
            single.push(Number(c.val()));
          }
          else {
            paired.push(Number(c.val()));
          }
        });

        var paras = {};
        _.forEach(PIPELINE.parameters, function (p) {
          paras[p.name] = p.value;
        });

        // Create the parameter object;
        var params = {};
        if($.isNumeric(ref)){
          params['ref'] = ref;
        }
        if(single.length>0) {
          params['single'] = single;
        }
        if(paired.length>0) {
          params['paired'] = paired;
        }
        if(_.keys(paras).length>0) {
          params['paras'] = paras;
        }
        params['name'] = angular.element("#pipeline-name").val();


        $http({
          url     : PIPELINE.url,
          method  : 'POST',
          dataType: 'json',
          params  : params,
          headers : {
            "Content-Type": "application/json"
          }
        })
          .success(function (data) {
            if (data.result === 'success') {
              vm.success = true;
            }
            else {
              if (data.error) {
                vm.error = data.error;
              }
              else if (data.parameters) {
                vm.paramError = data.parameters;
              }
            }
          });
      }
    };
  }

  function ParameterModalController($modal) {
    var vm = this;

    vm.openModal = function () {
      $modal.open({
        templateUrl: '/parameters.html',
        controller : 'ParameterController as paras'
      });
    };
  }

  function ParameterController($modalInstance) {
    var vm = this;
    PIPELINE.defaults = PIPELINE.defaults || angular.copy(PIPELINE.parameters);
    vm.parameters = angular.copy(PIPELINE.parameters);

    vm.update = function () {
      PIPELINE.parameters = angular.copy(vm.parameters);
      $modalInstance.close();
    };

    vm.close = function () {
      $modalInstance.dismiss();
    };

    vm.reset = function (index) {
      vm.parameters[index] = angular.copy(PIPELINE.defaults[index]);

    };
  }

  angular.module('irida.pipelines', [])
    .controller('PipelineController', ['$http', PipelineController])
    .controller('ParameterModalController', ["$modal", ParameterModalController])
    .controller('ParameterController', ['$modalInstance', ParameterController])
  ;
})();