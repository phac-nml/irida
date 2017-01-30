import {dom} from './../../../../../utilities/datatables.utilities';
import {EVENTS} from './../../constants';

function TableController(DTOptionsBuilder,
                         DTColumnBuilder,
                         LinelistTableService,
                         $scope) {
  this.dtOptions = DTOptionsBuilder
    .fromFnPromise(function() {
      return LinelistTableService.getMetadata();
    })
    .withDOM(dom)
    .withScroller()
    .withOption('scrollX', true)
    .withOption('deferRender', true)
    .withOption('scrollY', '50vh');

  const headers = LinelistTableService.getColumns();

  this.dtColumns = headers.map(header => {
    return DTColumnBuilder
      .newColumn(header)
      .withTitle(header)
      .renderWith(data => {
        // This is where any custom rendering logic should go.
        // example formatting date columns.
        return data.value;
      });
  });

  $scope.$on(EVENTS.TABLE.columnVisibility, (e, args) => {
    this.dtColumns[args.index].visible = args.selected;
  });
}

TableController.$inject = [
  'DTOptionsBuilder',
  'DTColumnBuilder',
  'LinelistTableService',
  '$scope'
];

export const TableComponent = {
  template: `
<table datatable="" 
  class="table" 
  dt-options="$ctrl.dtOptions" 
  dt-columns="$ctrl.dtColumns">

</table>`,
  require: {
    parent: '^^linelist'
  },
  controller: TableController
};
