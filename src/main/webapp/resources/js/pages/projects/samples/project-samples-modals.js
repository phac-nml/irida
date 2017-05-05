(function (ng, $, moment, Clipboard, Project, page, project) {
  "use strict";

  /**
   * Handles all AngularUI modal opening functionality
   * @param $uibModal
   * @returns {object}
   */
  function modalService($uibModal) {

    function _copyMoveModal(config) {
      return $uibModal.open({
        templateUrl: config.url + "&" + $.param({sampleIds: config.selectedSamples}),
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
            };
          };

          vm.cancel = function () {
            $uibModalInstance.dismiss();
          };

          vm.doCopy = function () {
            $uibModalInstance.close({
              sampleIds: config.selectedSamples,
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
     * @param ids
     * @returns {*}
     */
    function openRemoveModal(ids) {
      return $uibModal.open({
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
     * @param sampleIds - ids for samples to merge
     * @returns {*}
     */
    function openMergeModal(sampleIds) {
      return $uibModal.open({
        templateUrl : page.urls.modals.merge + "?" + $.param({sampleIds: sampleIds}),
        openedClass : 'merge-modal',
        controllerAs: "mergeCtrl",
        controller  : "MergeController",
        resolve     : {
          samples: function () {
            return sampleIds;
          }
        }
      }).result;
    }

    function openFilterModal() {
      var ids = page.ajaxParam.associated || [];
      ids.unshift(page.project.id);
      return $uibModal.open({
        templateUrl: page.urls.modals.filter + "?" + $.param({projectIds: ids}),
        controllerAs: "filterCtrl",
        openedClass : 'filter-modal',
        controller: "FilterModalController"
      }).result;
    }

    function openLinkerModal(ids) {
      var modal = $uibModal.open({
        templateUrl: 'linker.tmpl.html',
        openedClass: 'linker-modal',
        controllerAs: "lCtrl",
        controller: ["$window", "$uibModalInstance", "ids", function($window, $uibModalInstance, ids) {
          var cmd = page.linker;
          if (typeof ids !== 'undefined' && ids.length > 0) {
            // Need to find out if all the samples are selected.
            var all = $window.oTable_samplesTable.page.info().recordsTotal === ids.length;
            if (!all) {
              let samples = ' -s '; // Start adding sampels
              samples += ids.join(' -s ');
              cmd += samples;
            }
          }
          this.cmd = cmd;

          this.close = $uibModalInstance.close;
        }],
        resolve: {
          ids: function() {
            return ids;
          }
        }
      });

      // Set up copy to clipboard functionality.
      modal.opened.then(function () {
        new Clipboard(".clipboard-btn");
      });

      return modal.result;
    }

    function openGalaxyModal(ids, projectId) {
      return $uibModal.open({
        templateUrl: page.urls.modals.galaxy,
        controllerAs: "gCtrl",
        controller: "GalaxyDialogCtrl",
        resolve    : {
          sampleIds: function () {
            return ids;
          },
          openedByCart: function () {
            return false;
          },
          multiProject: function () {
            return false;
          },
          projectId: function () {
            return projectId;
          }
        }
      });
    }

    return {
      openFilterModal            : openFilterModal,
      openMoveModal              : openMoveModal,
      openCopyModal              : openCopyModal,
      openRemoveModal            : openRemoveModal,
      openMergeModal             : openMergeModal,
      openLinkerModal            : openLinkerModal,
      openGalaxyModal            : openGalaxyModal
    };
  }

  /**
   * Controller for handling interaction with the merge samples modal.
   * @param $uibModalInstance
   * @param samples
   * @constructor
   */
  function MergeModalController($uibModalInstance, sampleIds) {
    var vm = this;
    vm.selected = sampleIds[0];

    // If user enters a custom name it is not allowed to have spaces
    vm.validNameRE = /^[a-zA-Z0-9-_]+$/;

    /**
     * Closes the modal window without making any changes
     */
    vm.cancel = function () {
      $uibModalInstance.dismiss();
    };


    vm.doMerge = function () {
      $uibModalInstance.close({
        ids          : sampleIds,
        mergeSampleId: vm.selected,
        newName      : vm.name
      });
    };
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
      currState = {};
    ng.copy(defaultState, currState);

    this.getState = function() {
      var state = {};
      ng.copy(currState, state);
      return state;
    };

    this.setState = function(s) {
      ng.copy(s, currState);
      $rootScope.$broadcast("FILTER_TABLE", {filter: {
        organism: s.organism,
        name: s.name,
        date: {
          min: !!s.date.startDate  ? s.date.startDate.valueOf() : "",
          end: !!s.date.endDate  ? s.date.endDate.valueOf() : ""
        }
      }});
    };

    $rootScope.$on('CLEAR_FILTERS', function () {
      ng.copy(defaultState, currState);
    });

    $rootScope.$on('FILTER_CLEARED', function (event, args) {
      if(args.type === 'endDate' || args.type === 'endDate') {
        delete currState.date[args.type];
      } else if(currState.hasOwnProperty(args.type)) {
        delete currState[args.type];
      }
    });
  }

  /**
   * Controller for handling filtering samples by properties
   * @param $uibModalInstance
   * @param stateService
   * @constructor
   */
  function FilterModalController($uibModalInstance, stateService) {
    var vm = this;
    vm.filter = stateService.getState();

    vm.options = {ranges: {}};
    vm.options.ranges[page.i18n.dateFilter.days30] = [moment().subtract(30, 'days'), moment()];
    vm.options.ranges[page.i18n.dateFilter.days60] = [moment().subtract(60, 'days'), moment()];
    vm.options.ranges[page.i18n.dateFilter.days120] = [moment().subtract(120, 'days'), moment()];

    if(vm.filter.date.startDate !== null) {
      vm.options.startDate = vm.filter.date.startDate;
    }
    if(vm.filter.date.endDate !== null) {
      vm.options.endDate = vm.filter.date.endDate;
    }

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
      $uibModalInstance.dismiss();
    };
  }

  function selectInput () {
    return {
      restrict: 'A',
      link: function(scope, elm, attrs) {
        $(elm).select2();
      }
    };
  }

  ng.module("irida.projects.samples.modals", ["irida.projects.samples.service", "irida.directives.select2", "ngMessages", "ui.bootstrap", "daterangepicker"])
    .factory("modalService", ["$uibModal", modalService])
    .service("FilterStateService", ["$rootScope", FilterStateService])
    .directive("selectInput", selectInput)
    .controller("MergeController", ["$uibModalInstance", "samples", MergeModalController])
    .controller("FilterModalController", ["$uibModalInstance", "FilterStateService", FilterModalController])
  ;
}(window.angular, window.jQuery, window.moment, window.Clipboard, window.Project, window.PAGE, window.project));
