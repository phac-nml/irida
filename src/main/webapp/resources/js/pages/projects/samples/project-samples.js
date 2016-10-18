/*globals oTable_samplesTable */
(function (ng, page) {
  "use strict";

  /**
   * Directive to read in a file to filter the projects sample name by.
   * Usage: <input type="file" name="file" id="filter-file-input" class="inputfile"
   *               filter-by-file="filterCtrl.filterByFile($fileContent)"/>
   */
  var filterByFile = (function () {
    function filterByFile($parse) {
      return {
        restrict: 'A',
        scope   : false,
        link    : function (scope, element, attrs) {
          var fn = $parse(attrs.filterByFile);

          element.on('change', function (onChangeEvent) {
            var reader = new FileReader();

            reader.onload = function (onLoadEvent) {
              scope.$apply(function () {
                fn(scope, {$fileContent: onLoadEvent.target.result});
              });
            };

            reader.readAsText((onChangeEvent.srcElement || onChangeEvent.target).files[0]);
          });
        }
      }
    }

    return filterByFile;
  }());

  var AssociatedProjectsController = (function () {
    var scope, sampleService, visible = [];
    function AssociatedProjectsController($rootScope, SampleService) {
      scope = $rootScope;
      sampleService = SampleService;

      /**
       * Things to do when there is a change to the number of
       * associated projects to display.
       */
      scope.$on('ASSOCIATED_PROJECTS_CHANGE', function() {
        // Update the table
        sampleService.updateAssociatedProjects(visible);

        // Update the select all checkbox
        var select = $("#select-all-cb");

        // Reset checkbox
        select.prop('checked', false);
        select.prop("indeterminate", false);

        // Set new value on checkbox
        if (visible.length === $(".associated-cb").length) {
          select.prop('checked', true);
        } else if (visible.length !== 0) {
          select.prop("indeterminate", true);
        }
      });
    }

    /**
     * This method is used to display / hide an associated project in the table.
     * @param $event - click event
     */
    AssociatedProjectsController.prototype.toggleAssociatedProject = function ($event) {
      // If the input is click we need to prevent the default since we will select it later and need
      // to know the state of it before the click event.
      // IMP: This is because selection is allowed by clicking anywhere on the dropdown associated with the checkbox
      //      or the project name - giving a larger click area.
      if(!$($event.target).is('input')) {
        $event.preventDefault();
      }
      // Need to stop all propagation since this is an anchor tag.
      $event.stopPropagation();
      $event.stopImmediatePropagation();
      var target   = $($event.currentTarget),
          id       = target.data("id"), // This is the id for the project.
          index    = visible.indexOf(id), // Checking to see if the selected project is currently visible
          checkbox = $(target.find("input:checkbox")); // Find the checkbox so we can select it later.

      // Check to see if the project is already on the page,
      if (index > -1) {
        visible.splice(index, 1);
        checkbox.prop('checked', false);
      }
      else {
        visible.push(id);
        checkbox.prop('checked', true);
      }
      scope.$broadcast('ASSOCIATED_PROJECTS_CHANGE', {count: visible.length});
    };

    /**
     * Select all associated projects available to this project
     */
    AssociatedProjectsController.prototype.selectAllOrNone = function($event) {
      // If the input is click we need to prevent the default since we will select it later and need
      // to know the state of it before the click event.
      // IMP: This is because selection is allowed by clicking anywhere on the dropdown associated with the checkbox
      //      or the project name - giving a larger click area.
      if (!$($event.target).is('input')) {
        $event.preventDefault();
      }
      // Need to stop all propagation since this is an anchor tag.
      $event.stopPropagation();
      $event.stopImmediatePropagation();
      var target = $($event.currentTarget),
        id = target.data("id"), // This is the id for the project.
        checkbox = $(target.find("input:checkbox")),
        alinks = $(".associated-link"); // Find the checkbox so we can select it later.

      // Get all the ids required
      if (checkbox.prop('checked')) {
        alinks
          .each(function(index, item) {
            var link = $(item);
            link.find("input:checkbox").prop('checked', false);
            visible = [];
          });
        checkbox.prop('checked', false);
      } else {
        alinks
          .each(function(index, item) {
            var link = $(item),
              id = link.data('id');

            if (visible.indexOf(id) < 0) {
              link.find("input:checkbox").prop('checked', true);
              visible.push(id);
            }
          });
        checkbox.prop('checked', true);
      }
      scope.$broadcast('ASSOCIATED_PROJECTS_CHANGE', {count: visible.length});
    }

    return AssociatedProjectsController;
  }());

  /**
   * Controller to handle all function in the table menu.
   */
  var ToolsController = (function () {
    var modalService;
    var sampleService;
    var cartService;

    function ToolsController($scope, ModalService, SampleService, CartService) {
      var vm = this;
      vm.disabled = {};
      modalService = ModalService;
      sampleService = SampleService;
      cartService = CartService;

      /**
       * Responsible for setting the boolean enabled conditions
       * for the menu items.
       * @param {number} count The number of items in the selected.
       */
      function setButtonState(count) {
        vm.disabled.lessThanOne = count < 1;
        vm.disabled.lessThanTwo = count < 2;
      }

      /**
       * When a sample is selected the state of the menu needs to be set.
       */
      $scope.$on("SAMPLE_SELECTION_EVENT", function (event, args) {
        setButtonState(args.count);
      });

      /**
       * When the number of associated project changes, this changes the state
       * of the menu options.
       */
      $scope.$on("ASSOCIATED_PROJECTS_CHANGE", function(event, args) {
        vm.disabled.hasAssociated = args.count > 0;
      });

      setButtonState(0);
    }

    /**
     * Helper function to get the selected its for this project only
     * Since associated projects might also be in the list.
     * @returns {*}
     */
    function getProjectSelectedIds() {
      var allIds = datatable.getSelectedIds();
      // Only want this projects
      return allIds[page.project.id];
    }

    /**
     * Merge Samples
     */
    ToolsController.prototype.merge = function () {
      if (!this.disabled.lessThanTwo) {
        var ids = getProjectSelectedIds();
        modalService.openMergeModal(ids).then(function(result) {
          // This happens when the user click merge in the modal window.
          sampleService.merge(result).then(function() {
            // Once samples are merged, clear the table selections
            // and reload the table.
            datatable.clearSelected();
            oTable_samplesTable.ajax.reload(null, false);
          });
        });
      }
    };

    /**
     * Copy Samples
     */
    ToolsController.prototype.copy = function () {
      if(!this.disabled.lessThanOne) {
          var ids = getProjectSelectedIds();
          modalService.openCopyModal(ids).then(function (result) {
              sampleService.copy(result)
                  .then(function () {
                      datatable.clearSelected();
                  })
          });
      }
    };

    /**
     * Move Samples
     */
    ToolsController.prototype.move = function () {
      if(!this.disabled.lessThanOne) {
          var ids = getProjectSelectedIds();
          modalService.openMoveModal(ids).then(function (result) {
              sampleService.move(result)
                  .then(function () {
                    // Once samples are moved, clear the table selections
                    // and reload the table.
                      datatable.clearSelected();
                      oTable_samplesTable.ajax.reload(null, false);
                  })
          });
      }
    };

    /**
     * Remove samples from the current project.
     */
    ToolsController.prototype.remove = function () {
      if(!this.disabled.lessThanOne) {
        var ids = getProjectSelectedIds();
        modalService.openRemoveModal(ids).then(function () {
          sampleService.remove(ids)
            .then(function () {
              // Once samples are removed, clear the table selections
              // and reload the table.
              datatable.clearSelected();
              oTable_samplesTable.ajax.reload(null, false);
            })
        });
      }
    };

    /**
     * Add samples to the global cart
     */
    ToolsController.prototype.addToCart = function () {
      cartService.add(datatable.getSelectedIds())
          .then(function () {
            // Once added to the cart, clear the selected samples.
            datatable.clearSelected();
          });
    };

    /**
     * Download the selected samples.
     */
    ToolsController.prototype.download = function () {
      var selected = datatable.getSelectedIds();
      if (selected[page.project.id] && selected[page.project.id].length > 0) {
        sampleService.download(selected[page.project.id]);
      }
    };

    /**
     * Export selected samples using the NCBI Export Tool
     */
    ToolsController.prototype.ncbiExport = function () {
      var selected = datatable.getSelectedIds();
      if (selected[page.project.id] && selected[page.project.id].length > 0) {
        sampleService.ncbiExport(selected[page.project.id]);
      }
    };

    /**
     * Display modal for command line linker
     */
    ToolsController.prototype.linker = function () {
      var selected = datatable.getSelectedIds();
      modalService.openLinkerModal(selected[page.project.id]);
    };

    /**
     * Export to galaxy
     */
    ToolsController.prototype.galaxy = function () {
      var ids = datatable.getSelectedIds();
      modalService.openGalaxyModal(ids, page.project.id);
    };

    /**
     * Export table to excel or csv.
     * @param type
     */
    ToolsController.prototype.exportToFile = function (type) {
      sampleService.exportToFile(type);
    };

    return ToolsController;
  }());

  /**
   * Controller for handling the filtering of the datatable.
   */
  var FilterController = (function () {
    var service, scope;
    function FilterController(sampleService, $scope, modalService) {
      var vm = this;
      vm.fileFilterDisabled = false;
      service = sampleService;
      scope = $scope;
      this.modalService = modalService;
    }

    /**
     * Filter the table via a text file.
     * This uses the filterByFile direective.
     * @param $fileContent
     */
    FilterController.prototype.filterByFile = function ($fileContent) {
      // Make sure the names are unique and no extra spaces before and after.
      var samplesNames = _.uniq($fileContent.match(/[^\r\n]+/g)
        .map(function(name) { return name.trim() }));
      service.filterBySampleNames(samplesNames);
      // Clear the file name when complete.
      document.querySelector("#filter-file-input").value = "";
    };

    /**
     * Handles the clicking of the clear button.
     */
    FilterController.prototype.clearAll = function() {
      scope.$emit("CLEAR_FILTERS");
    };

    /**
     * Open the advance filter modal.
     */
    FilterController.prototype.openFilterModal = function() {
      this.modalService.openFilterModal();
    };

    return FilterController;
  }());

  /**
   * Directive for adding the filter buttons to the table.
   */
  var samplesFilter = (function () {
    function samplesFilter() {
      return {
        replace: true,
        templateUrl: 'filter.html',
        controllerAs: 'filterCtrl',
        controller: ["SampleService", "$scope", "modalService", FilterController]
      };
    }

    return samplesFilter;
  }());

  /**
   * Directive to handle the filter tags.
   */
  var filteredTags = (function () {
    function filteredTags() {
      return {
        replace     : true,
        templateUrl : 'filtered-tags.html',
        controllerAs: 'tags',
        controller  : ['$scope', function ($scope) {
          var vm = this;
          vm.tag = {};

          /**
           * Check to see if the tags need to change state based if
           * there are any associated projects displayed.
           */
          $scope.$on("ASSOCIATED_PROJECTS_CHANGE", function (event, args) {
            if(args.count > 0) {
              vm.tag = {};
            }
          });

          /**
           * Request to display tags for all currently applied filters.
           */
          $scope.$on("FILTER_TABLE", function (event, args) {
            ng.extend(vm.tag, args.filter);
          });

          /**
           * Request to clear all currently applied filters.
           */
          $scope.$on("CLEAR_FILTERS", function () {
            vm.tag = {};
          });

          /**
           * Request to display the filter by file tag
           */
          $scope.$on('FILE_FILTER', function () {
            $scope.$apply(function () {
              vm.tag.file = true;
            });
          });

          /**
           * Close a particular tag/filter
           * @param type The filter to remove
           */
          vm.close = function (type) {
            delete vm.tag[type];
            $scope.$broadcast('FILTER_CLEARED', {type: type});
          };
        }]
      };
    }

    return filteredTags;
  }());

  /**
   * Angular controller to handle selecting samples by page or entire project
   */
  var SelectionController = (function() {
    var $window, _sampleService, vm;

    function SelectionController($scope, window, sampleService) {
      vm = this;
      $window = window;
      _sampleService = sampleService;
      var allSelectedCB = document.querySelector("#allSelectedCB");

      $scope.$on("SAMPLE_SELECTION_EVENT", function(event, args) {
        allSelectedCB.indeterminate = false;
        allSelectedCB.checked = false;
        if(args.count === 0) {
          vm.allSelected = false;
          return;
        }
        vm.allSelected = $window.oTable_samplesTable.page.info().recordsTotal === args.count;
        if (vm.allSelected) {
          allSelectedCB.checked = true;
        } else {
          allSelectedCB.indeterminate = true;
        }
      });
    }

    /**
     * Select all the samples in the datatable;
     * @private
     */
    function _selectAllSamples() {
      _sampleService.getAllIds()
        .then(function(result) {
          $window.datatable.selectAll(result.data);
        });
    }

    /**
     * Deselect all the samples in the datatable.
     * @private
     */
    function _deselectAllSamples() {
      $window.datatable.clearSelected();
    }

    /**
     * Event handler for clicking on the select all samples button.
     */
    SelectionController.prototype.selectAllBtn = function($event) {
      if($event.target.id === "allSelectedCB") {
        $event.stopPropagation();
      }

      if(!vm.allSelected) {
        _selectAllSamples()
      } else {
        _deselectAllSamples();
      }
    };

    /**
     * Event handler for clicking on the select all samples in the dropdown menu
     */
    SelectionController.prototype.selectAll = function() {
      vm.allSelected = true;
      _selectAllSamples()
    };

    /**
     * Event handler for clicking on the select none button in the dropdown menu.
     * Deselects all samples in the project.
     */
    SelectionController.prototype.selectNone = function() {
      vm.allSelected = false;
      _deselectAllSamples();
    };

    /**
     * Event handler for clicking on the select page button in the dropdown menu.
     * Selects all samples on the current page in the datatable.
     */
    SelectionController.prototype.selectPage = function() {
      $window.datatable.selectPage();
    };

    /**
     * Event handler for clicking on the deselect page button in the dropdown menu.
     * All samples on the current page will be deselected.
     */
    SelectionController.prototype.deselectPage = function() {
      $window.datatable.deselectPage()
    };

    return SelectionController;
  }());

    ng.module("irida.projects.samples.controller", ["irida.projects.samples.modals", "irida.projects.samples.service"])
      .directive('filterByFile', ["$parse", filterByFile])
      .directive('samplesFilter', [samplesFilter])
      .directive('filteredTags', [filteredTags])
    .controller('AssociatedProjectsController', ["$rootScope", "SampleService", AssociatedProjectsController])
    .controller('ToolsController', ["$scope", "modalService", "SampleService", "CartService", ToolsController])
      .controller('SelectionController', ['$scope', '$window', "SampleService", SelectionController])
  ;
}(window.angular, window.PAGE));
