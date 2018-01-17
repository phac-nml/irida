(function(ng, $, location, page) {
  "use strict";
  /**
   * Main controller for the pipeline launch page.
   * @param $http AngularJS http object
   * @param CartService a reference to the cart service (to clear it)
   * @param ParameterService for passing parameter information between modal and page
   * @param DynamicSourceService for selecting parameters from a Galaxy Tool Data Table
   * @constructor
   */
  function PipelineController(
    $scope,
    $http,
    CartService,
    ParameterService,
    DynamicSourceService
  ) {
    var vm = this;

    vm.parameters = ParameterService.getOriginalSettings();
    vm.selectedParameters = ParameterService.getSelectedParameters();
    vm.dynamicSources = DynamicSourceService.getSettings();
    vm.selectedDynamicSource = DynamicSourceService.getSelectedGalaxyToolDataTableField();

    $scope.$on("PARAMETERS_SAVED", function() {
      vm.selectedParameters = ParameterService.getSelectedParameters();
    });

    $scope.$on("REFERENCE_FILE_UPLOADED", function(event, uploaded) {
      vm.uploadedReferenceFile = uploaded.id;
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
     * Update the selected tool data table field in the tool data table service
     * for the modal dialog whenever we select a new tool data table field
     * from the drop-down.
     */
    vm.dynamicSourceValueSelected = function(dynamicSourceValue) {
      DynamicSourceService.setSelectedGalaxyToolDataTableField(
        dynamicSourceValue,
        vm.selectedDynamicSource
      );
    };

    /**
     * Launch the pipeline
     */
    vm.launch = function() {
      var // reference file id (use the most recently uploaded or the selected one)
        ref =
          typeof vm.uploadedReferenceFile !== "undefined"
            ? vm.uploadedReferenceFile
            : Number(ng.element("option:selected").val()),
        // User defined name for the pipeline
        name = ng.element("#pipeline-name").val(),
        // Whether or not to write results back to samples
        writeResultsToSamples = $("#share-results-samples").is(":checked"),
        // All the selected sample single or pair-end files
        radioBtns = ng.element("input[type='radio']:checked"),
        // Holds all the ids for the selected single-end
        single = [],
        // Holds all the ids for the selected paired-end
        paired = [],
        // User-written description of the analysis
        description = ng.element("#analysis-description").val(),
        // Projects to share results with
        shared = [];

      if (name === null || name.length === 0) {
        vm.error = page.i18n.required;
      } else {
        // Hide the launch buttons and display a message that it has been sent.
        vm.loading = true;

        // Get a list of paired and single end files to run.
        radioBtns.each(function(c) {
          c = $(this);

          if (c.attr("data-type") === "single_end") {
            single.push(Number(c.val()));
          } else {
            paired.push(Number(c.val()));
          }
        });

        ng.element(".share-project:checked").each(function() {
          var box = $(this);
          shared.push(box.val());
        });

        var currentSettings = ParameterService.getSelectedParameters()
          .currentSettings;
        var currentDynamicSourceSettings = DynamicSourceService.getSettings()
          .currentSettings;

        var selectedParameters = {
          id: currentSettings.id,
          parameters: currentSettings.parameters
        };
        if (Object.keys(currentDynamicSourceSettings).length > 0) {
          var dynamicSourceParameters = Object.values(
            currentDynamicSourceSettings
          ).map(({ label, value, name }) => ({ label, value, name }));
          selectedParameters.parameters = selectedParameters.parameters.concat(
            dynamicSourceParameters
          );
        }

        // Create the parameter object;
        var params = {};
        if ($.isNumeric(ref)) {
          params["ref"] = ref;
        }
        if (single.length > 0) {
          params["single"] = single;
        }
        if (paired.length > 0) {
          params["paired"] = paired;
        }

        if (
          Object.keys(selectedParameters).length > 0 &&
          selectedParameters.id !== "no_parameters"
        ) {
          params["selectedParameters"] = selectedParameters;
        }
        params["name"] = name;
        params["description"] = description;
        params["writeResultsToSamples"] = writeResultsToSamples;

        if (shared.length > 0) {
          params["sharedProjects"] = shared;
        }

        $http({
          url: page.urls.startUrl,
          method: "POST",
          dataType: "json",
          params: params,
          headers: {
            "Content-Type": "application/json"
          }
        }).then(function(response) {
          var data = response.data;
          if (data.success) {
            vm.success = true;
          } else {
            if (data.error) {
              vm.error = data.error;
            } else if (data.parameterError) {
              vm.paramError = data.parameters;
            } else if (data.pipelineError) {
              window.notifications.show({
                type: "error",
                text: data.pipelineError
              });
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
    vm.removeSample = function(projectId, sampleId) {
      CartService.removeSample(projectId, sampleId).then(function() {
        ng.element("#sample-" + sampleId).remove();
        if (ng.element(".sample-container").length === 0) {
          location.reload();
        }
      });
    };

    /**
     * Clear the cart and redirect to the projects page
     */
    vm.clearAndRedirect = function() {
      var clearPromise = CartService.clear();

      // after the cart is cleared, redirect the browser
      clearPromise.then(function() {
        window.location = page.urls.projectsPage;
      });
    };
  }

  /**
   * Opens the modal dialog when the "Customize" button is pressed.
   * @param $uibModal reference to the modal dialog.
   */
  function ParameterModalController($uibModal) {
    var vm = this;

    vm.openModal = function() {
      $uibModal.open({
        templateUrl: "/parameters.html",
        controller: "ParameterController as paras"
      });
    };
  }

  /**
   * Controller for handling interaction with the modal dialog.
   *
   * @param $rootScope the root scope
   * @param $http angular http reference
   * @param $uibModalInstance the modal dialog
   * @param ParameterService the service for handling parameter state
   */
  function ParameterController(
    $rootScope,
    $http,
    $uibModalInstance,
    ParameterService
  ) {
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
    vm.update = function() {
      ParameterService.parametersModified = true;
      if (vm.saveParameters) {
        vm.saveAndUse();
      }
      $uibModalInstance.close();
    };

    /**
     * Straight up close the modal.
     */
    vm.close = function() {
      $uibModalInstance.dismiss();
    };

    /**
     * Reset the specified value back to the default value.
     * @param index the index in the list of parameters we can set.
     */
    vm.reset = function(index) {
      ParameterService.resetCurrentSelectionIndex(index);
    };

    /**
     * Persist the current parameter set to the server, add the
     * saved set to the list of parameters on the page, and select
     * the parameters.
     */
    vm.saveAndUse = function() {
      var parametersToSave = {
        pipelineId: page.pipeline.pipelineId,
        parameterSetName: vm.parameterSetName,
        // vm.selectedParameters.parameters is an array of maps, this will reduce it down
        // into a single map with key-value pairs from each parameter name to its corresponding
        // value. The final parameter to reduce is the empty map, that's our initial state.
        parameterValues: vm.selectedParameters.parameters.reduce(function(
          prev,
          curr
        ) {
          prev[curr.name] = curr.value;
          return prev;
        },
        {})
      };

      $http({
        url: page.urls.saveParametersUrl,
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        transformRequest: undefined,
        data: JSON.stringify(parametersToSave)
      }).then(function(response) {
        var data = response.data;
        $uibModalInstance.dismiss();
        // on success, we can re-use the selected parameters in
        // this controller; update the id and label, then append
        // it to PIPELINE.parameters to have it magically appear!
        ParameterService.resetCurrentSelection();
        vm.selectedParameters.id = data.id;
        vm.selectedParameters.label = vm.parameterSetName;
        ParameterService.addSettingsToFront(vm.selectedParameters);
        $rootScope.$emit("PARAMETERS_SAVED");
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

    // Check to see if there are any parameters, if not put a default
    if (page.pipeline.parameters.length === 0) {
      page.pipeline.parameters.push({
        id: "no_parameters",
        label: "",
        parameters: []
      });
    }

    /**
     * Duplicated copy of the original set of parameters on the page
     * so that we can quickly roll back to default values for any
     * parameter set.
     */
    var originalSettings = page.pipeline.parameters.map(function(params) {
      return {
        currentSettings: ng.copy(params),
        defaultSettings: ng.copy(params)
      };
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
        currentSettings: ng.copy(settingsToAdd),
        defaultSettings: ng.copy(settingsToAdd)
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
      selectedParameters.currentSettings.parameters[index] = ng.copy(
        selectedParameters.defaultSettings.parameters[index]
      );
    };

    /**
     * Completely reset the current settings back to the set of default values.
     */
    svc.resetCurrentSelection = function() {
      selectedParameters.currentSettings = ng.copy(
        selectedParameters.defaultSettings
      );
    };
  }

  /**
   * Service for handling Galaxy Tool Data Tables.
   */
  function DynamicSourceService() {
    var svc = this;

    // Check to see if there are any tool data tables, if not put a default
    if (page.pipeline.dynamicSources == null) {
      page.pipeline.dynamicSources = [
        {
          id: "no_dynamic_sources",
          label: "",
          parameters: []
        }
      ];
    }

    var settings = {};
    if (page.pipeline.dynamicSources != null) {
      settings["currentSettings"] = {};
      settings["availableSettings"] = {};
      for (var i = 0; i < page.pipeline.dynamicSources.length; i++) {
        settings.availableSettings[page.pipeline.dynamicSources[i].id] =
          page.pipeline.dynamicSources[i];
      }
    }

    /**
     * Get the settings that the page currently has.
     */
    svc.getSettings = function() {
      return settings;
    };

    /**
     * Get the currently selected parameters from the page.
     */
    svc.getSelectedGalaxyToolDataTableField = function(galaxyToolDataTable) {
      return settings.currentSettings[galaxyToolDataTable];
    };

    /**
     * Set the current tool data table field on the page.
     * @param galaxyToolDataTable the Galaxy Tool Data Table to set
     * @param currentSelection the tool data table field that is currently selected
     */
    svc.setSelectedGalaxyToolDataTableField = function(
      galaxyToolDataTable,
      currentSelection
    ) {
      settings.currentSettings[galaxyToolDataTable] = currentSelection;
    };
  }

  function FileUploadCtrl($rootScope, Upload) {
    var vm = this;

    vm.referenceUploadStarted = false;

    vm.upload = function(files) {
      if (files && files.length > 0) {
        vm.referenceUploadStarted = true;
        Upload.upload({
          url: page.urls.upload,
          file: files[0]
        })
          .progress(function(evt) {
            vm.progress = parseInt(100.0 * evt.loaded / evt.total);
          })
          .then(
            function(response) {
              var data = response.data;
              vm.uploaded = {
                id: data["uploaded-file-id"],
                name: data["uploaded-file-name"]
              };
              vm.uploadError = false;
              $rootScope.$emit("REFERENCE_FILE_UPLOADED", vm.uploaded);
              vm.referenceUploadStarted = false;
            },
            function(response) {
              vm.referenceUploadStarted = false;
              window.notifications.show({
                text: response.data.error,
                type: "error"
              });
            }
          );
      }
    };
  }

  const pipelineModule = ng
    .module("irida.pipelines", ["irida.cart", "ngFileUpload"])
    .controller("PipelineController", [
      "$rootScope",
      "$http",
      "CartService",
      "ParameterService",
      "DynamicSourceService",
      PipelineController
    ])
    .controller("ParameterModalController", [
      "$uibModal",
      ParameterModalController
    ])
    .controller("ParameterController", [
      "$rootScope",
      "$http",
      "$uibModalInstance",
      "ParameterService",
      ParameterController
    ])
    .controller("FileUploadCtrl", ["$rootScope", "Upload", FileUploadCtrl])
    .service("ParameterService", [ParameterService])
    .service("DynamicSourceService", [DynamicSourceService]).name;

  ng.module("irida").requires.push(pipelineModule);
})(window.angular, window.jQuery, window.location, window.PAGE);
