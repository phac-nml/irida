(function () {
  "use strict";
  /**
   * Main controller for the pipeline launch page.
   * @param $http AngularJS http object
   * @param CartService a reference to the cart service (to clear it)
   * @param notifications notifications
   * @param ParameterService for passing parameter information between modal and page
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
    
    /**
     * Update the selected parameters in the parameter service
     * for the modal dialog whenever we select a new set of parameters
     * from the drop-down.
     */
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
      paired = [],
      remoteSingle = [],
      remotePaired = [];

      if (name === null || name.length === 0) {
        vm.error = PIPELINE.required;
      } else {
        // Hide the launch buttons and display a message that it has been sent.
        vm.loading = true;

        // Get a list of paired and single end files to run.
        _.forEach(radioBtns, function (c) {
          c = $(c);
          if(c.hasClass("remote")){
            if (c.attr('data-type') === 'single_end') {
              remoteSingle.push(c.val());
            }
            else {
              remotePaired.push(c.val());
            }
          }
          else{
            if (c.attr('data-type') === 'single_end') {
              single.push(Number(c.val()));
            }
            else {
              paired.push(Number(c.val()));
            }
          }
        });

        var currentSettings = ParameterService.getSelectedParameters().currentSettings;
        var selectedParameters = {
        		"id": currentSettings.id,
        		"parameters": currentSettings.parameters
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
        
        if(remoteSingle.length > 0){
          params['remoteSingle'] = remoteSingle;
        }
        if(remotePaired.length > 0){
          params['remotePaired'] = remotePaired;
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

    /**
	 * Remove a sample from the pipeline to be run.
	 * 
	 * @param projectId the project id of the sample to remove
	 * @param sampleId the sample if to remove
	 */
    vm.removeSample = function (projectId, sampleId) {
      CartService.removeSample(projectId,sampleId).then(function(){
        angular.element('#sample-' + sampleId).remove();
        if(angular.element('.sample-container').length === 0) {
          location.reload();
        }
      });
    };
    
    /**
	 * Remove a sample from the pipeline to be run.
	 * 
	 * @param projectId the project id of the sample to remove
	 * @param sampleId the sample if to remove
	 */
    vm.removeRemoteSample = function (sampleId) {
      CartService.removeRemoteSample(sampleId).then(function(){
        //need funky selection style here because css selectors don't like slashes
        angular.element("[id*='remote-sample-" + sampleId + "']").remove();
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

  /**
   * Opens the modal dialog when the "Customize" button is pressed.
   * @param $modal reference to the modal dialog.
   */
  function ParameterModalController($modal) {
    var vm = this;

    vm.openModal = function () {
      $modal.open({
        templateUrl: '/parameters.html',
        controller : 'ParameterController as paras'
      });
    };
  }

  /**
   * Controller for handling interaction with the modal dialog.
   * 
   * @param $rootScope the root scope
   * @param $http angular http reference
   * @param $modalInstance the modal dialog
   * @param ParameterService the service for handling parameter state
   */
  function ParameterController($rootScope, $http, $modalInstance, ParameterService) {
    var vm = this;

    vm.selectedParameters = ParameterService.getSelectedParameters().currentSettings;
    vm.parameterSetName = vm.selectedParameters.label;
    vm.parametersModified = ParameterService.parametersModified;
    vm.saveParameters = false;

    /**
     * When the "Use these parameters" button is pressed, mark 
     * that the parameters were updated in the service, optionally
     * save the parameters to the server and close the modal.
     */
    vm.update = function () {
      ParameterService.parametersModified = true;
      if (vm.saveParameters) {
    	  vm.saveAndUse();
      }
      $modalInstance.close();
    };

    /**
     * Straight up close the modal.
     */
    vm.close = function () {
      $modalInstance.dismiss();
    };

    /**
     * Reset the specified value back to the default value.
     * @param index the index in the list of parameters we can set.
     */
    vm.reset = function (index) {
    	ParameterService.resetCurrentSelectionIndex(index);
    };
    
    /**
     * Persist the current parameter set to the server, add the
     * saved set to the list of parameters on the page, and select
     * the parameters.
     */
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
    
    /**
     * If any value is changed, mark that we're modifying parameters by
     * changing the id of the parameter set to be custom (so the server knows
     * to not add a reference to a set of saved parameters) and add
     * a marker to the parameter set name to show the user that the 
     * params have been modified.
     */
    vm.valueChanged = function() {
    	vm.parametersModified = true;
    	vm.selectedParameters.id = "custom";
    	if (vm.parameterSetName.indexOf("(*)") == -1) {
    		vm.parameterSetName = vm.parameterSetName + " (*)";
    	}
    };
  }
  
  /**
   * Service for handling parameter state.
   */
  function ParameterService() {
	  var svc = this;
	  
	  /**
	   * Duplicated copy of the original set of parameters on the page
	   * so that we can quickly roll back to default values for any 
	   * parameter set.
	   */
	  var originalSettings = PIPELINE.parameters.map(function(params) {
	    	return {
	    		currentSettings: angular.copy(params),
	    		defaultSettings: angular.copy(params)
	    	}
	  });
	  
	  var selectedParameters = originalSettings[0];
	  
	  /**
	   * Get the settings that the page currently has.
	   */
	  svc.getOriginalSettings = function() {
		  return originalSettings;
	  };
	  
	  /**
	   * Add customized parameters to the drop-down, we'll duplicate
	   * the values in here so that we can go back to defaults.
	   * 
	   * @param settingsToAdd the settings to add to the current set of settings.
	   */
	  svc.addSettingsToFront = function(settingsToAdd) {
		  var savedParameters = {
				  currentSettings: angular.copy(settingsToAdd),
				  defaultSettings: angular.copy(settingsToAdd)
		  };
		  originalSettings.unshift(savedParameters);
		  selectedParameters = originalSettings[0];
	  };
	  
	  /**
	   * Get the currently selected parameters from the page.
	   */
	  svc.getSelectedParameters = function() {
		  return selectedParameters;
	  };
	  
	  /**
	   * Set the current set of parameters on the page.
	   * 
	   * @param currentSelection the parameters that are currently selected
	   */
	  svc.setSelectedParameters = function(currentSelection) {
		  selectedParameters = currentSelection;
	  };
	  
	  /**
	   * Reset one of the values in the currently selected 
	   * parameters back to its default value.
	   * @param index the index of the parameter to reset.
	   */
	  svc.resetCurrentSelectionIndex = function(index) {
		  selectedParameters.currentSettings.parameters[index] = angular.copy(selectedParameters.defaultSettings.parameters[index]);
	  };
	  
	  /**
	   * Completely reset the current settings back to the set of default values.
	   */
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