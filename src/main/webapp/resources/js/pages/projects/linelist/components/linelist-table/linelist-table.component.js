import {dom} from './../../../../../utilities/datatables.utilities';
import {EVENTS} from './../../constants';

function controller(DTOptionsBuilder,
                    DTColumnBuilder,
                    LinelistService,
                    $scope) {
  this.dtOptions = DTOptionsBuilder
    .fromFnPromise(() => {
      return LinelistService.getMetadata();
    })
    .withDOM(dom)
    .withScroller()
    .withOption('scrollX', true)
    .withOption('deferRender', true)
    .withOption('scrollY', '50vh')
    .withOption('scrollCollapse', true)
    .withColReorder()
    .withColReorderCallback(() => {
      this.parent.columnReorder(this.fnOrder());
    });

  this.dtColumns = this.headers.map(header => {
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

controller.$inject = [
  'DTOptionsBuilder',
  'DTColumnBuilder',
  'LinelistService',
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
  bindings: {
    headers: '<'
  },
  controller
};
