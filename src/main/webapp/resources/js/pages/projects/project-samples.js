(function (ng, page) {
  function updateSamplesTable(projects) {
    var params = "";
    projects.forEach(function (p) {
      params += "&associated=" + p;
    });
    oTable_samplesTable.ajax.url(page.urls.samples.project + '/?' + params).load();
  }

  function AssociatedProjectsController() {
    var vm = this, visible = [];

    vm.toggleAssociatedProject = function($event) {
      var target = $event.currentTarget,
          id = parseInt(target.dataset.id);
      if(target.checked) {
        visible.push(id);
      } else {
        visible.splice(visible.indexOf(id), 1);
      }
      updateSamplesTable(visible);
    };
  }

  ng.module("irida.projects.samples.controller", [])
    .controller('AssociatedProjectsController', [AssociatedProjectsController]);
  ;
}(window.angular, window.PAGE));