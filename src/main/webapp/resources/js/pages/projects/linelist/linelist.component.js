import {EVENTS} from './constants';

function controller(LinelistService, $scope) {
  this.headers = LinelistService.getColumns();
  this.metadata = LinelistService.getMetadata();

  this.updateColumnVisibility = function(column) {
    $scope.$broadcast(EVENTS.TABLE.columnVisibility, column);
  };
}

controller.$inject = ['LinelistService', '$scope'];

export const Linelist = {
  templateUrl: 'linelist.tmpl.html',
  controller
};
