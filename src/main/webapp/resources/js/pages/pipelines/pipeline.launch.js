(function () {
  "use strict";
  /**
   * Main controller for the pipeline launch page.
   * @param $http AngularJS http object
   * @constructor
   */
  function PipelineController($scope, $http, CartService, notifications, ParameterService) {
    var vm = this;

    vm.parameters = ParameterService.getOriginalSettings();
    vm.selectedParameters = ParameterService.getSelectedParameters();
    
    $scope.$on('PARAMETERS_SAVED', function() {
    	vm.selectedParameters = ParameterService.getSelectedParameters();
    });
    
    /*
     * Whether or not the page is waiting for a response from the server.
     */
    vm.loading = false;
    
    vm.parameterSelected = function() {
    	ParameterService.setSelectedParameters(vm.selectedParameters);
    };

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

        var selectedParameters = {
        		"id": ParameterService.getSelectedParameters().id,
        		"parameters": ParameterService.getSelectedParameters().parameters
        };

        // Create the parameter object;
        var params = {};
        if ($.isNumeric(ref)) {
          params['ref'] = ref;
        }
        if (single.length > 0) {
          params['single'] = single;
        }
        if (paired.length > 0) {
          params['paired'] = paired;
        }
        if (_.keys(selectedParameters).length > 0) {
          params['selectedParameters'] = selectedParameters;
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
            if (data.success) {
              vm.success = true;
            }
            else {
              if (data.error) {
                vm.error = data.error;
              }
              else if (data.parameterError) {
                vm.paramError = data.parameters;
              }
              else if(data.pipelineError) {
                notifications.show({type: 'error', msg: data.pipelineError});
              }
            }
          });
      }
    };

    vm.removeSample = function (projectId, sampleId) {
      CartService.removeSample(projectId,sampleId).then(function(){
        angular.element('#sample-' + sampleId).remove();
        if(angular.element('.sample-container').length === 0) {
          location.reload();
        }
      });
    };

    /**
     * Clear the cart and redirect to the projects page
     */
    vm.clearAndRedirect = function () {
      var clearPromise = CartService.clear();

      // after the cart is cleared, redirect the browser
      clearPromise.then(function () {
        window.location = projectsPage;
      })
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

  function ParameterController($rootScope, $http, $modalInstance, ParameterService) {
    var vm = this;

    vm.selectedParameters = ParameterService.getSelectedParameters().currentSettings;
    vm.parameterSetName = vm.selectedParameters.label;
    vm.parametersModified = ParameterService.parametersModified;
    vm.saveParameters = false;

    vm.update = function () {
      ParameterService.parametersModified = true;
      if (vm.saveParameters) {
    	  vm.saveAndUse();
      }
      $modalInstance.close();
    };

    vm.close = function () {
      $modalInstance.dismiss();
    };

    vm.reset = function (index) {
    	ParameterService.resetCurrentSelectionIndex(index);
    };
    
    vm.saveAndUse = function() {
      var parametersToSave = {
        pipelineId : PIPELINE.pipelineId,
        parameterSetName : vm.parameterSetName,
        // vm.selectedParameters.parameters is an array of maps, this will reduce it down
        // into a single map with key-value pairs from each parameter name to its corresponding
        // value. The final parameter to reduce is the empty map, that's our initial state.
        parameterValues : vm.selectedParameters.parameters.reduce(function (prev, curr) {
          prev[curr.name] = curr.value;
          return prev;
        }, {})
      };
    
      $http({
        url    : PIPELINE.saveParametersUrl,
        method : "POST",
        headers: {
          "Content-Type": "application/json"
        }, 
        transformRequest : undefined,
        data   : JSON.stringify(parametersToSave),
      }).success(function (data) {
    	  $modalInstance.dismiss();
    	  // on success, we can re-use the selected parameters in
    	  // this controller; update the id and label, then append
    	  // it to PIPELINE.parameters to have it magically appear!
    	  ParameterService.resetCurrentSelection();
    	  vm.selectedParameters.id = data.id;
    	  vm.selectedParameters.label = vm.parameterSetName;
    	  ParameterService.addSettingsToFront(vm.selectedParameters);
    	  $rootScope.$emit('PARAMETERS_SAVED');
      });
    };
    
    vm.valueChanged = function() {
    	vm.parametersModified = true;
    	vm.selectedParameters.id = "custom";
    	if (vm.parameterSetName.indexOf("(*)") == -1) {
    		vm.parameterSetName = vm.parameterSetName + " (*)";
    	}
    };
  }
  
  function ParameterService() {
	  var svc = this;
	  
	  var originalSettings = PIPELINE.parameters.map(function(params) {
	    	return {
	    		currentSettings: angular.copy(params),
	    		defaultSettings: angular.copy(params)
	    	}
	  });
	  
	  var selectedParameters = originalSettings[0];
	  
	  svc.getOriginalSettings = function() {
		  return originalSettings;
	  };
	  
	  svc.addSettingsToFront = function(settingsToAdd) {
		  var savedParameters = {
				  currentSettings: angular.copy(settingsToAdd),
				  defaultSettings: angular.copy(settingsToAdd)
		  };
		  originalSettings.unshift(savedParameters);
		  selectedParameters = originalSettings[0];
	  };
	  
	  svc.getSelectedParameters = function() {
		  return selectedParameters;
	  };
	  
	  svc.setSelectedParameters = function(currentSelection) {
		  selectedParameters = currentSelection;
	  };
	  
	  svc.resetCurrentSelectionIndex = function(index) {
		  selectedParameters.currentSettings.parameters[index] = angular.copy(selectedParameters.defaultSettings.parameters[index]);
	  };
	  
	  svc.resetCurrentSelection = function() {
		  selectedParameters.currentSettings = angular.copy(selectedParameters.defaultSettings);
	  }
  }

  angular.module('irida.pipelines', ['irida.cart'])
    .controller('PipelineController', ['$rootScope', '$http','CartService', 'notifications', 'ParameterService', PipelineController])
    .controller('ParameterModalController', ["$modal", ParameterModalController])
    .controller('ParameterController', ['$rootScope', '$http', '$modalInstance', 'ParameterService', ParameterController])
    .service('ParameterService', [ParameterService])
  ;
})();