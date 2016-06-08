/*globals oTable_samplesTable */
(function (ng, page) {
  "use strict";

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

    function ToolsController($scope, ModalService, SampleService) {
      var vm = this;
      modalService = ModalService;
      sampleService = SampleService;

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

    return ToolsController;
  }());

    ng.module("irida.projects.samples.controller", ["irida.projects.samples.modals", "irida.projects.samples.service"])
    .controller('AssociatedProjectsController', [AssociatedProjectsController])
    .controller('ToolsController', ["$scope", "modalService", "SampleService", ToolsController])
  ;
}(window.angular, window.PAGE));
