import {dom} from './../../../../../utilities/datatables.utilities';
import {EVENTS} from './../../constants';

function TableController(DTOptionsBuilder,
                         DTColumnBuilder,
                         LinelistTableService,
                         $scope, $compile) {
  const vm = this;

  this.dtOptions = DTOptionsBuilder
    .fromFnPromise(function() {
      return LinelistTableService.getMetadata();
    })
    .withDOM(dom)
    .withScroller()
    .withOption('scrollX', true)
    .withOption('deferRender', true)
    .withOption('scrollY', '50vh')
    .withOption('scrollCollapse', true)
    .withColReorder()
    .withColReorderCallback(function() {
      vm.parent.columnReorder(this.fnOrder());
    })
    .withOption('drawCallback', () => {
      const div = document.querySelector('.toolbar');
      div.innerHTML = `<metadata-component></metadata-component>`;
      $compile(div)($scope);
    });

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
    const column = args.column;
    if (column) {
      this.dtColumns[column.index].visible = column.selected;
    }
  });
}

TableController.$inject = [
  'DTOptionsBuilder',
  'DTColumnBuilder',
  'LinelistTableService',
  '$scope',
  '$compile'
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
