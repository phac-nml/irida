(function (ng, page) {
  "use strict";

  /**
   * Handles all AngularUI modal opening functionality
   * @param $uibModal
   * @returns {{openMergeModal: openMergeModal, openAssociatedProjectsModal: openAssociatedProjectsModal}}
   */
  function modalService($uibModal) {

    function openRemoveModal(selectedSamples) {
      var ids = [];
      selectedSamples.forEach(function (item) {
        ids.push(item.sample.identifier);
      });
      return $uibModal.open({
        size        : 'lg',
        templateUrl : page.urls.modals.remove + "?" + $.param({sampleIds: ids}),
        openedClass : 'remove-modal',
        controllerAs: "removeCtrl",
        controller  : ["$uibModalInstance", function ($uibModalInstance) {
          var vm = this;

          vm.cancel = function () {
            $uibModalInstance.dismiss();
          };

          vm.remove = function () {
            $uibModalInstance.close();
          };
        }]
      }).result;
    }

    /**
     * Open the modal to handle merging samples
     * @param selectedSamples - samples to merge
     * @returns {*}
     */
    function openMergeModal(selectedSamples) {
      var ids = selectedSamples.map(function (item) {
        return item.sample.identifier;
      });
      return $uibModal.open({
        templateUrl : page.urls.modals.merge + "?" + $.param({sampleIds: ids}),
        controllerAs: "mergeCtrl",
        controller  : "MergeController",
        resolve     : {
          samples: function () {
            return selectedSamples;
          }
        }
      }).result;
    }

    /**
     * Open the modal to display/hide associated projects
     * @param currentlyDisplayed
     * @returns {*}
     */
    function openAssociatedProjectsModal(currentlyDisplayed) {
      return $uibModal.open({
        templateUrl: "associated-projects.modal.html",
        controllerAs: "associatedProjectsCtrl",
        controller: "AssociatedProjectsModalController",
        resolve: {
          display: function () {
            return currentlyDisplayed;
          }
        }
      }).result;
    }

    return {
      openRemoveModal            : openRemoveModal,
      openMergeModal             : openMergeModal,
      openAssociatedProjectsModal: openAssociatedProjectsModal
    };
  }

  /**
   * Controller for handling interaction with the associated projects display modal
   * @param $uibModalInstance - AngularUI modal instance
   * @param associatedProjectsService
   * @param display - Projects to display
   * @constructor
   */
  function AssociatedProjectsModalCtrl($uibModalInstance, associatedProjectsService, display) {
    var vm = this;
    vm.projects = {};
    vm.display = ng.copy(display);
    vm.local = {};

    // Get the local project
    associatedProjectsService.getLocal().then(function (result) {
      // Check to see if they are already displayed.
      result.data.forEach(function(project) {
        project.selected = vm.display.local.indexOf(project.identifier) > -1;
      });
      vm.projects.local = result.data;
    });

    vm.close = function () {
      $uibModalInstance.dismiss();
    };

    vm.showProjects = function () {
      // Just want the ids
      vm.display.local = [];
      vm.projects.local.forEach(function (project) {
        if (project.selected) {
          vm.display.local.push(project.identifier);
        }
      });

      $uibModalInstance.close(vm.display);
    };
  }

  /**
   * Controller for handling interaction with the merge samples modal.
   * @param $uibModalInstance
   * @param samples
   * @constructor
   */
  function MergeModalController($uibModalInstance, samples) {
    var vm = this;
    vm.samples = samples;
    vm.selected = vm.samples[0].sample.identifier;

    // If user enters a custom name it is not allowed to have spaces
    vm.validNameRE = /^[a-zA-Z0-9-_]+$/;

    vm.cancel = function () {
      $uibModalInstance.dismiss();
    };


    vm.doMerge = function () {
      // Get the sampleIds to merge
      var ids = samples.map(function (item) {
        return item.sample.identifier;
      });
      $uibModalInstance.close({
        ids          : ids,
        mergeSampleId: vm.selected,
        newName      : vm.name
      });
    }
  }

  ng.module("irida.projects.samples.modals", ["irida.projects.samples.service", "ui.bootstrap"])
    .factory("modalService", ["$uibModal", modalService])
    .controller("AssociatedProjectsModalController", ["$uibModalInstance", "associatedProjectsService", "display", AssociatedProjectsModalCtrl])
    .controller("MergeController", ["$uibModalInstance", "samples", MergeModalController])
  ;
}(angular, PAGE));