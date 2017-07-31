import { EVENTS } from "./constants";

/**
 * Controller for the entire LineList page
 * @param {function} LinelistService service to handle getting linelist fields.
 * @param {object} $scope Angular dom scoppe
 */
function controller(LinelistService, $scope) {
  this.fields = [];

  this.$onInit = function onInit() {
    this.fields = LinelistService.getHeaders();
  };

  this.updateColumnVisibility = (column, index) => {
    $scope.$broadcast(EVENTS.TABLE.columnVisibility, { column, index });
  };

  this.columnReorder = columns => {
    $scope.$broadcast(EVENTS.TABLE.colReorder, { columns });
  };

  this.templateSelected = fields => {
    $scope.$broadcast(EVENTS.TABLE.template, { fields });
  };
}

controller.$inject = ["LinelistService", "$rootScope"];

export const Linelist = {
  templateUrl: "linelist.tmpl.html",
  controller
};
