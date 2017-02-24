import {dom} from './../../../../../utilities/datatables.utilities';
import {EVENTS} from './../../constants';

/**
 * Controller for the metadata linelist Datatables
 * @param {object} $scope angular dom scope
 * @param {object} DTOptionsBuilder Datatables option builder
 * @param {object} DTColumnBuilder Datatables column builder
 * @param {object} LinelistService service to get information for the linelist
 */
function controller($scope,
                    DTOptionsBuilder,
                    DTColumnBuilder,
                    LinelistService) {
  const $ctrl = this;
  $ctrl.table = {};

  /**
   * Angular controller initialization
   *  - Setting upt the table options and columns
   */
  $ctrl.$onInit = () => {
    $ctrl.dtOptions = DTOptionsBuilder
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
      .withColReorderCallback(function() {
        $ctrl.parent.columnReorder(this.fnOrder());
      });

    $ctrl.dtColumns = $ctrl.fields
      .map(header => {
        const col = DTColumnBuilder
          .newColumn(header)
          .withTitle(header)
          .renderWith(data => {
            // This is where any custom rendering logic should go.
            // example formatting date columns.
            return data.value;
          });
        col.visible = true;
        return col;
      });
  };

  /**
   * Listen for changes to the column visibility called from the Metadata sidebar.
   */
  $scope.$on(EVENTS.TABLE.columnVisibility, (e, args) => {
    const {column} = args;
    const col = $ctrl.dtColumns
      .find(c => {
        return c.sTitle === column.label;
      });

    col.visible = column.visible;
  });

  /**
   * Listen for changes to which template is to be displayed in the table.
   */
  $scope.$on(EVENTS.TABLE.template, (e, args) => {
    const {fields} = args;
    const order = $ctrl.table.DataTable.colReorder.order();

    let openColumn = fields.length; // Start of the non-template fields
    $ctrl.dtColumns.forEach((column, index) => {
      // See if the column should be displayed based on the template
      // and find out where.
      const fieldIndex = fields.findIndex(field => {
        return field.label === column.sTitle;
      });
      if (fieldIndex > -1) {
        order[fieldIndex] = index;
        column.visible = true;
      } else {
        order[openColumn] = index;
        openColumn += 1;
        column.visible = false;
      }
    });
    // Complete the actual reorder in the Datatables.
    $ctrl.dtOptions.withColReorderOrder(order);
  });
}

controller.$inject = [
  '$scope',
  'DTOptionsBuilder',
  'DTColumnBuilder',
  'LinelistService'
];

export const TableComponent = {
  template: `
<table datatable="" 
  class="table" 
  width="100%"
  dt-options="$ctrl.dtOptions" 
  dt-columns="$ctrl.dtColumns"
  dt-instance="$ctrl.table">
</table>`,
  require: {
    parent: '^^linelist'
  },
  bindings: {
    fields: '<',
    metadata: '<'
  },
  controller
};
