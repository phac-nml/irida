import "DataTables/datatables";
import "DataTables/datatables-fixedColumns";
import "DataTables/datatables-scroller";
import "DataTables/datatables-colreorder";
import $ from "jquery";
import { tableConfig } from "../../../../utilities/datatables-utilities";
import { EVENTS } from "../constants";

function defineTable() {
  const config = Object.assign({}, tableConfig, {
    serverSide: false,
    scrollY: 800,
    scrollCollapse: true,
    scrollX: true,
    scroller: true,
    colReorder: true,
    data: window.metadataList,
    fixedColumns: {
      leftColumns: 1
    },
    columnDefs: window.headersList.map(function(header, index) {
      return {
        targets: index,
        render(data, type, full) {
          let value = full[header].value;
          // This is where any custom rendering logic should go.
          // example formatting date columns.
          if (
            header === "label" &&
            full.hasOwnProperty("id") &&
            full.hasOwnProperty("label")
          ) {
            return `
<a class="btn btn-link" 
   href="${window.PAGE.urls.sample}${full.id.value}/details">${value}</a>
`;
          }
          return value;
        }
      };
    })
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
    console.log(fields);

    // The `order` will be and array of the current ordering (e.g.: [1, 2, 4, 5, 3])
    // in relation to its initial order.
    const order = table.colReorder.order();
    console.log(order);

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
    console.log(order);

    // TODO: something wrong here!
    table.colReorder.order(order);
    table.columns.adjust().draw(false);
  });
}

LineListTableController.$inject = ["$scope"];
