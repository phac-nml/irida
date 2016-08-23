/*globals oTable_samplesTable */
(function (ng, page) {
  "use strict";

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
      if (index > -1) {
        visible.splice(index, 1);
        checkbox.prop('checked', false);
      }
      else {
        visible.push(id);
        checkbox.prop('checked', true);
      }
      sampleService.updateAssociatedProjects(visible);
      scope.$broadcast('ASSOCIATED_PROJECTS_CHANGE', {count: visible.length});
    };

    return AssociatedProjectsController;
  }());

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

      function setButtonState(count) {
        vm.disabled.lessThanOne = count < 1;
        vm.disabled.lessThanTwo = count < 2;
      }

      $scope.$on("SAMPLE_SELECTION_EVENT", function (event, args) {
        setButtonState(args.count);
      });

      $scope.$on("ASSOCIATED_PROJECTS_CHANGE", function(event, args) {
        vm.disabled.hasAssociated = args.count > 0;
      });

      setButtonState(0);
    }

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
          sampleService.merge(result).then(function() {
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
                      datatable.clearSelected();
                      oTable_samplesTable.ajax.reload(null, false);
                  })
          });
      }
    };

    ToolsController.prototype.remove = function () {
      if(!this.disabled.lessThanOne) {
        var ids = getProjectSelectedIds();
        modalService.openRemoveModal(ids).then(function () {
          sampleService.remove(ids)
            .then(function () {
              datatable.clearSelected();
              oTable_samplesTable.ajax.reload(null, false);
            })
        });
      }
    };

    ToolsController.prototype.addToCart = function () {
      cartService.add(datatable.getSelectedIds())
          .then(function () {
            datatable.clearSelected();
          });
    };

    ToolsController.prototype.download = function () {
      var selected = datatable.getSelectedIds();
      if (selected[page.project.id] && selected[page.project.id].length > 0) {
        sampleService.download(selected[page.project.id]);
      }
    };

    ToolsController.prototype.ncbiExport = function () {
      var selected = datatable.getSelectedIds();
      if (selected[page.project.id] && selected[page.project.id].length > 0) {
        sampleService.ncbiExport(selected[page.project.id]);
      }
    };

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

    ToolsController.prototype.exportToFile = function (type) {
      sampleService.exportToFile(type);
    };

    return ToolsController;
  }());

  var FilterController = (function () {
    var service, scope;
    function FilterController(sampleService, $scope, modalService) {
      var vm = this;
      vm.fileFilterDisabled = false;
      service = sampleService;
      scope = $scope;
      this.modalService = modalService;

      scope.$on("ASSOCIATED_PROJECTS_CHANGE", function (event, args) {
        vm.fileFilterDisabled = args.count > 0;
      });
    }

    FilterController.prototype.filterByFile = function ($fileContent) {
      var samplesNames = $fileContent.match(/[^\r\n]+/g);
      service.filterBySampleNames(samplesNames);
      document.querySelector("#filter-file-input").value = "";
    };

    FilterController.prototype.clearAll = function() {
      scope.$emit("CLEAR_FILTERS");
    };

    FilterController.prototype.openFilterModal = function() {
      this.modalService.openFilterModal();
    };

    return FilterController;
  }());

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

  var SelectionController = (function() {
    var $window, _sampleService, vm;

    function SelectionController($scope, window, sampleService) {
      vm = this;
      $window = window;
      _sampleService = sampleService;

      $scope.$on("SAMPLE_SELECTION_EVENT", function(event, args) {
        vm.allSelected = $window.oTable_samplesTable.page.info().recordsTotal === args.count;
        vm.allSelectedCB = vm.allSelected;
      });
    }
    
    function selectAllSamples() {
      _sampleService.getAllIds()
        .then(function(result) {
          $window.datatable.selectAll(result.data);
        });
    }
    
    function deselectAllSamples() {
      $window.datatable.clearSelected();
    }

    SelectionController.prototype.selectAllBtn = function() {
      if(!vm.allSelected) {
        selectAllSamples()
      } else {
        deselectAllSamples();
      }
    };

    SelectionController.prototype.selectAll = function() {
      vm.allSelected = true;
      selectAllSamples()
    };

    SelectionController.prototype.selectNone = function() {
      vm.allSelected = false;
      deselectAllSamples();
    };

    SelectionController.prototype.selectPage = function() {
      $window.datatable.selectPage();
    };

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
