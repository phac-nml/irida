import {EVENTS} from './constants';

function LinelistController($scope) {
  this.updateColumnVisibility = function(column) {
    $scope.$broadcast(EVENTS.TABLE.columnVisibility, column);
  };
}

export const Linelist = {
  templateUrl: 'linelist.tmpl.html',
  controller: LinelistController
};
