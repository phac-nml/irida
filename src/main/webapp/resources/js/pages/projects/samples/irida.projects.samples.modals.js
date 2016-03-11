(function (ng) {
  "use strict";

  function modalService($uibModal) {
    function openMergeModal() {

    }

    function openAssociatedProjectsModal(currentlyDisplayed) {
      return $uibModal.open({
        templateUrl: "associated-projects.modal.html",
        controllerAs: "associatedProjectsCtrl",
        controller: "AssociatedProjectsModalCtrl",
        resolve: {
          display: function () {
            return currentlyDisplayed;
          }
        }
      }).result;
    }

    return {
      openAssociatedProjectsModal: openAssociatedProjectsModal
    };
  }

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

  ng.module("irida.projects.samples.modals", ["irida.projects.samples.service", "ui.bootstrap"])
    .factory("modalService", ["$uibModal", modalService])
    .controller("AssociatedProjectsModalCtrl", ["$uibModalInstance", "associatedProjectsService", "display", AssociatedProjectsModalCtrl])
  ;
}(angular));