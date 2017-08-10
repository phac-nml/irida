import "DataTables/datatables";
import "DataTables/datatables-colreorder";
import "DataTables/datatables-fixedColumns";
import $ from "jquery";
import { createItemLink, tableConfig } from "../../../../utilities/datatables-utilities";
import { EVENTS } from "../constants";

const $table = $("#linelist");

function defineTable() {
  const columnDefs = (() => {
    const cols = [
      {
        targets: 0,
        render(data, type, full) {
          return createItemLink({
            url: `${window.PAGE.urls.sample}${full.id.value}/details`,
            label: full["irida-sample-name"].value
          });
        }
      }
    ];

    // Copy the headers list, and remove the label (at index 0).
    const headers = $table.find("th");
    headers.each(function(index) {
      if (index !== 0) {
        const text = $(this).text();
        cols.push({
          targets: index,
          render(data, type, full) {
            return full[text].value;
          }
        });
      }
    });
    return cols;
  })();

  const config = Object.assign(tableConfig, {
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

  return $table.DataTable(config);
}

export function LineListTableController($rootScope, $scope) {
  const $ctrl = this;
  let table;
  $ctrl.$onInit = () => {
    table = defineTable();

    // Broadcast when one of the columns have been reordered
    table.on("column-reorder", function(e, settings, details) {
      if (!details.drop) {
        $rootScope.$broadcast(EVENTS.TABLE.colReorder, {
          order: Array.from(table.colReorder.order())
        });
      }
    });
  };

  /**
   * Listen for changes to the column visibility called from the Metadata sidebar.
   */
  $scope.$on(EVENTS.TABLE.columnVisibility, (e, args) => {
    const { column, index } = args;
    // index +1 to account for label.
    table.column(index + 1).visible(column.visible);
  });

  $scope.$on(EVENTS.TABLE.reset, () => {
    table.columns().every(function() {
      this.visible(true, false);
    });
    table.colReorder.reset();
  });

  /**
   * Listen for changes to which template is to be displayed in the table.
   */
  $scope.$on(EVENTS.TABLE.template, (e, args) => {
    const { fields } = args;

    // Create the initial order of the table.
    const order = table.colReorder.order();

    // This will be the position in the order for the next non-template field.
    // +1 for the label column
    let openColumn = fields.length + 1;

    table.colReorder.reset();
    table.columns().every(function(index) {
      // Always want the label visible and in the right order.
      // NEVER mess with the label.  Label at index == 0
      if (index !== 0) {
        const column = this;
        const header = this.header().innerHTML;

        // See if the column is in the template fields, and capture its index
        // to add to the new order.
        let templateIndex = fields.findIndex(field => {
          return header.localeCompare(field.label) === 0;
        });

        if (templateIndex > -1) {
          templateIndex += 1; // Need to leave room for the label

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
      }
    });

    $rootScope.$broadcast(EVENTS.TABLE.colReorder, {
      order: Array.from(order)
    });
    table.colReorder.order(order);
  });
}

LineListTableController.$inject = ["$rootScope", "$scope"];
