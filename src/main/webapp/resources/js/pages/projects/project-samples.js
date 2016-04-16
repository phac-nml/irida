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
    AssociatedProjectsController.prototype.toggleAssociatedProject = function ($event) {
      var target = $event.currentTarget, id = parseInt(target.dataset.id);
      if (target.checked) {
        this.visible.push(id);
      }
      else {
        this.visible.splice(this.visible.indexOf(id), 1);
      }
      this.updateSamplesTable();
    };
    return AssociatedProjectsController;
  }());
  
  ng.module("irida.projects.samples.controller", [])
    .controller('AssociatedProjectsController', [AssociatedProjectsController]);
  ;
}(window.angular, window.PAGE));