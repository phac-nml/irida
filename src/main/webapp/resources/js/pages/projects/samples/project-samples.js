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
    function AssociatedProjectsController() {
      this.visible = [];
    }
    AssociatedProjectsController.prototype.updateSamplesTable = function () {
      var params = "";
      this.visible.forEach(function (p) {
        params += "&associated=" + p;
      });
      oTable_samplesTable.ajax.url(page.urls.samples.project + '/?' + params).load();
    };

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
          index    = this.visible.indexOf(id), // Checking to see if the selected project is currently visible
          checkbox = $(target.find("input:checkbox")); // Find the checkbox so we can select it later.
      if (index > -1) {
        this.visible.splice(index, 1);
        checkbox.prop('checked', false);
      }
      else {
        this.visible.push(id);
        checkbox.prop('checked', true);
      }
      this.updateSamplesTable();
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

    return ToolsController;
  }());

  var FilterController = (function () {
    var service, scope;
    function FilterController(sampleService, $scope) {
      var vm = this;
      vm.ddEnabled = false;
      service = sampleService;
      scope = $scope;

      scope.$on("SAMPLE_SELECTION_EVENT", function (event, args) {
        if(args.count > 0) {
          vm.ddEnabled = true;
        }
      });
    }

    FilterController.prototype.openFileSelect = function () {
      // setTimeout allows angularjs digest cycle to complete.
      setTimeout(function () {
        document.querySelector("#filter-file-input").click();
      }, 0);
    };

    FilterController.prototype.filterByFile = function ($fileContent) {
      var samplesNames = $fileContent.match(/[^\r\n]+/g);
      service.filterBySampleNames(samplesNames);
    };

    return FilterController;
  }());

  var samplesFilter = (function () {
    function samplesFilter() {
      return {
        replace: true,
        templateUrl: 'filter.html',
        controllerAs: 'filterCtrl',
        controller: ["SampleService", "$scope", FilterController]
      };
    }

    return samplesFilter;
  }());

  var filteredTags = (function () {
    var  reloadCmds = {
      file: 'CLEAR_FILE_FILTER'
    };
    function filteredTags() {
      return {
        replace     : true,
        templateUrl : 'filtered-tags.html',
        controllerAs: 'tags',
        controller  : ['$scope', function ($scope) {
          var vm = this;
          vm.tag = {
            file: false
          };

          $scope.$on('FILE_FILTER', function () {
            vm.tag.file = true;
          });

          vm.close = function (type) {
            vm.tag[type] = false;
            $scope.$broadcast(reloadCmds[type]);
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
    .controller('AssociatedProjectsController', [AssociatedProjectsController])
    .controller('ToolsController', ["$scope", "modalService", "SampleService", "CartService", ToolsController])
  ;
}(window.angular, window.PAGE));
