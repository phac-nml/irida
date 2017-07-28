import "DataTables/datatables";
import "DataTables/datatables-fixedColumns";
import "DataTables/datatables-colreorder";
import $ from "jquery";
import {
  tableConfig,
  createItemLink
} from "../../../../utilities/datatables-utilities";
import { EVENTS } from "../constants";

function defineTable() {
  const columnDefs = (() => {
    const cols = [];

    // Copy the headers list, and remove the label (at index 0).
    const headers = Array.from(window.headersList);
    headers.forEach((header, index) => {
      if (index === 0) {
        // This is the label.  Needs to be a link to the sample.
        cols.push({
          targets: 0,
          render(data, type, full) {
            return createItemLink({
              url: `${window.PAGE.urls.sample}${full.id.value}/details`,
              label: full.label.value
            });
          }
        });
      } else {
        cols.push({
          targets: index,
          render(data, type, full) {
            return full[header].value;
          }
        });
      }
    });
    return cols;
  })();

  const config = Object.assign({}, tableConfig, {
    data: window.metadataList,
    serverSide: false,
    paging: false,
    scrollY: "600px",
    scrollCollapse: true,
    scrollX: true,
    fixedColumns: {
      leftColumns: 1
    },
    colReorder: {
      fixedColumnsLeft: 1
    },
    scroller: true,
    columnDefs
  });

  return $("#linelist").DataTable(config);
}

export function LineListTableController($scope) {
  const $ctrl = this;
  let table;
  $ctrl.$onInit = () => {
    table = defineTable();
  };

  /**
   * Listen for changes to the column visibility called from the Metadata sidebar.
   */
  $scope.$on(EVENTS.TABLE.columnVisibility, (e, args) => {
    const { column } = args;
    const index = window.headersList.indexOf(column.label);
    table.column(index).visible(column.visible);
  });

  /**
   * Listen for changes to which template is to be displayed in the table.
   */
  $scope.$on(EVENTS.TABLE.template, (e, args) => {
    const { fields } = args;

    // The `order` will be and array of the current ordering (e.g.: [1, 2, 4, 5, 3])
    // in relation to its initial order.
    const order = table.colReorder.order();

    // This will be the position in the order for the next non-template field.
    let openColumn = fields.length;

    table.columns().every(function(index) {
      const column = this;
      const header = this.header().innerHTML;

      // See if the column is in the template fields, and capture its index
      // to add to the new order.
      const templateIndex = fields.findIndex(field => {
        return field.label === header;
      });

      if (templateIndex > -1) {
        // Column has been found in the template.
        // Add the column's index (this is the columns original index when the table was created)
        // to the order array and the position it is required in the template.
        // E.g. If it is originally the 2nd column, but needs to be in the first position [2, ...]
        order[templateIndex] = index;
        // Only template columns should be visible
        column.visible(true, false);
      } else {
        // Column not found in the template.
        // Add it to the next open column position - these are after the template slots.
        order[openColumn] = index;
        openColumn += 1;
        // Only template columns should be visible
        column.visible(false, false);
      }
    });

    // TODO: something wrong here!
    table.colReorder.order(order);
    table.columns.adjust().draw(false);
  });
}

LineListTableController.$inject = ["$scope"];
