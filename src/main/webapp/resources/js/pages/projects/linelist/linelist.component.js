import {EVENTS} from './constants';

function LinelistController($scope) {
  this.updateColumnVisibility = column => {
    $scope.$broadcast(EVENTS.TABLE.columnVisibility, {column});
  };

  this.columnReorder = columns => {
    $scope.$broadcast(EVENTS.TABLE.colReorder, {columns});
  };
}

export const Linelist = {
  templateUrl: 'linelist.tmpl.html',
  controller: LinelistController
};
