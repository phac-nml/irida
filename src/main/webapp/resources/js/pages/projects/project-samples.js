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
      if(!$($event.target).is('input')) {
        $event.preventDefault();
      }
      $event.stopPropagation();
      $event.stopImmediatePropagation();
      var target = $($event.currentTarget),
          id = target.data("id"),
          index = this.visible.indexOf(id),
          checkbox = $(target.find("input:checkbox"));
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
  
  ng.module("irida.projects.samples.controller", [])
    .controller('AssociatedProjectsController', [AssociatedProjectsController]);
  ;
}(window.angular, window.PAGE));