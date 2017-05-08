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
          .renderWith((data, type, full) => {
            // This is where any custom rendering logic should go.
            // example formatting date columns.
            if (header === 'label' &&
              full.hasOwnProperty('id') && full.hasOwnProperty('label')) {
              return `
<a class="btn btn-link" 
   href="${window.PAGE.urls.sample}${full.id.value}/details">${data.value}</a>
`;
            }

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
    const templateFields = args.fields;

    // The `order` will be and array of the current ordering (e.g.: [1, 2, 4, 5, 3])
    // in relation to its initial order.
    const order = $ctrl.table.DataTable.colReorder.order();

    // This will be the position in the order for the next non-template field.
    let openColumn = templateFields.length;

    // We need to update the order of the table to reflect the current template.
    // This goes through each column int the table
    $ctrl.dtColumns.forEach((column, index) => {
      // See if the column is in the template fields, and capture its index
      // to add to the new order.
      const templateIndex = templateFields.findIndex(field => {
        return field.label === column.sTitle;
      });

      if (templateIndex > -1) {
        // Column has been found in the template.
        // Add the column's index (this is the columns original index when the table was created)
        // to the order array and the position it is required in the template.
        // E.g. If it is originally the 2nd column, but needs to be in the first position [2, ...]
        order[templateIndex] = index;
        // Only template columns should be visible
        column.visible = true;
      } else {
        // Column not found in the template.
        // Add it to the next open column position - these are after the template slots.
        order[openColumn] = index;
        openColumn += 1;
        // Only template columns should be visible
        column.visible = false;
      }
    });
    // Ask Datatables to display the updated order.
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
