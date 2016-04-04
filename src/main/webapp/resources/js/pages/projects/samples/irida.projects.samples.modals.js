(function (ng, page, project) {
  "use strict";

  /**
   * Handles all AngularUI modal opening functionality
   * @param $uibModal
   * @returns {{openMergeModal: openMergeModal, openAssociatedProjectsModal: openAssociatedProjectsModal}}
   */
  function modalService($uibModal) {

    function _getSampleIds(samples) {
      return samples.map(function(item) {
        return item.sample.identifier;
      })
    }

    function _copyMoveModal(config) {
      var ids = _getSampleIds(config.selectedSamples);
      return $uibModal.open({
        templateUrl: config.url + "&" + $.param({sampleIds: ids}),
        openedClass: "copy-modal",
        controllerAs: "copyModalCtrl",
        controller  : ["$uibModalInstance", function ($uibModalInstance) {
          var vm = this;

          vm.generateSelect2 = function () {
            return function (data) {
              return data.projects.filter(function (p) {
                return p.identifier != project.id;
              }).map(function (p) {
                return ({
                  id  : p.identifier,
                  text: p.text || p.name
                });
              });
            }
          };

          vm.cancel = function () {
            $uibModalInstance.dismiss();
          };

          vm.doCopy = function () {
            $uibModalInstance.close({
              sampleIds: ids,
              newProjectId: vm.project
            });
          };
        }]
      }).result;
    }

    function openMoveModal(selectedSamples) {
      return _copyMoveModal({
        selectedSamples: selectedSamples,
        url: page.urls.modals.move
      });
    }

    function openCopyModal(selectedSamples) {
      return _copyMoveModal({
        selectedSamples: selectedSamples,
        url: page.urls.modals.copy
      });
    }

    /**
     * Open the modal to remove samples from a project.
     * @param selectedSamples
     * @returns {*}
     */
    function openRemoveModal(selectedSamples) {
      var ids = _getSampleIds(selectedSamples);
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
        openedClass : 'merge-modal',
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

    function openFilterModal() {
      return $uibModal.open({
        templateUrl: "filter.modal.html",
        controllerAs: "filterCtrl",
        openedClass : 'filter-modal',
        controller: "FilterModalController"
      }).result;
    }

    return {
      openFilterModal            : openFilterModal,
      openMoveModal              : openMoveModal,
      openCopyModal              : openCopyModal,
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

    vm.rowClick = function($event) {
      $event.stopPropagation();
    };

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

    /**
     * Closes the modal window without making any changes
     */
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

  /**
   * Container for the current state of the samples filter.
   * @constructor
   */
  function FilterStateService($rootScope) {
    var defaultState = {
      date: {
        startDate: null,
        endDate: null
      }
    },
      currState = _.clone(defaultState);

    this.getState = function() {
      return _.clone(currState);
    };

    this.setState = function(s) {
      currState = s;
    };

    $rootScope.$on('CLEAR_FILTER', function () {
      currState = _.clone(defaultState);
    });
  }

  /**
   * Controller for handling filtering samples by properties
   * @param $uibModalInstance
   * @constructor
   */
  function FilterModalController($uibModalInstance, stateService) {
    var vm = this;
    vm.filter = stateService.getState();
    vm.options = {ranges: {}};
    vm.options.ranges[page['i18n']['dateFilter']['days30']] = [moment().subtract(30, 'days'), moment()];
    vm.options.ranges[page['i18n']['dateFilter']['months6']] = [moment().subtract(6, 'months'), moment()];

    /**
     * Closes the modal window without making any changes
     */
    vm.cancel = function() {
      $uibModalInstance.dismiss();
    };

    /**
     * Return the filter state to the calling function
     */
    vm.doFilter = function() {
      stateService.setState(vm.filter);
      $uibModalInstance.close(vm.filter);
    };
  }

  ng.module("irida.projects.samples.modals", ["irida.projects.samples.service", "irida.directives.select2", "ui.bootstrap", "daterangepicker"])
    .factory("modalService", ["$uibModal", modalService])
    .service("FilterStateService", ["$rootScope", FilterStateService])
    .controller("AssociatedProjectsModalController", ["$uibModalInstance", "AssociatedProjectsService", "display", AssociatedProjectsModalCtrl])
    .controller("MergeController", ["$uibModalInstance", "samples", MergeModalController])
    .controller("FilterModalController", ["$uibModalInstance", "FilterStateService", FilterModalController])
  ;
}(window.angular, window.PAGE, window.project));
