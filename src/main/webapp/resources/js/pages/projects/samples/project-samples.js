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
      modalService = ModalService;
      sampleService = SampleService;
      cartService = CartService;

      function setButtonState(count) {
        vm.disabled = {
          lessThanOne: count < 1,
          lessThanTwo: count < 2
        };
      }

      $scope.$on("SAMPLE_SELECTION_EVENT", function (event, args) {
        setButtonState(args.count);
      });

      setButtonState(0);
    }

      /**
       * Merge Samples
       */
    ToolsController.prototype.merge = function () {
      if (!this.disabled.lessThanTwo) {
        var ids = datatable.getSelectedIds();
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
          var ids = datatable.getSelectedIds();
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
          var ids = datatable.getSelectedIds();
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
        var ids = datatable.getSelectedIds();
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
      cartService.add(page.project.id, datatable.getSelectedIds())
          .then(function () {
            datatable.clearSelected();
          });
    };

    ToolsController.prototype.download = function () {
      var ids = datatable.getSelectedIds();
      if (ids.length > 0) {
        sampleService.download(ids);
      }
    };

    ToolsController.prototype.ncbiExport = function () {
      var ids = datatable.getSelectedIds();
      if (ids.length > 0) {
        sampleService.ncbiExport(ids);
      }
    };

    ToolsController.prototype.linker = function () {
      var ids = datatable.getSelectedIds();
      modalService.openLinkerModal(ids);
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

    ng.module("irida.projects.samples.controller", ["irida.projects.samples.modals", "irida.projects.samples.service"])
      .directive('filterByFile', ["$parse", filterByFile])
      .directive('samplesFilter', [samplesFilter])
      .directive('filteredTags', [filteredTags])
    .controller('AssociatedProjectsController', ["$rootScope", "SampleService", AssociatedProjectsController])
    .controller('ToolsController', ["$scope", "modalService", "SampleService", "CartService", ToolsController])
  ;
}(window.angular, window.PAGE));
