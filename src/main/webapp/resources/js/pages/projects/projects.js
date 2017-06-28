import "DataTables/datatables";
import "DataTables/datatables-buttons";
import $ from "jquery";
import {formatDateDOM} from "Utilities/date-utilities";
import {createItemLink, generateColumnOrderInfo, dom} from "Utilities/datatables-utilities";

// Column look-ups for quick referencing
const COLUMNS = generateColumnOrderInfo();

/**
 * Download table in specified format.f
 * @param {string} format format of downloaded doc.
 */
function downloadItem({format = "xlsx"}) {
  const url = `${window.PAGE.urls.export}&dtf=${format}`;
  const anchor = document.createElement("a");
  anchor.style.display = "none";
  anchor.href = url;
  anchor.click();
}

if (typeof window.PAGE === "object") {
  $("#projects").DataTable({
    dom,
    // Set up the export buttons.
    // These are loaded through the PAGE object.
    buttons: [
      {
        extend: "collection",
        text() {
          return document.querySelector("#export-btn-text").innerHTML;
        },
        // The buttons are loaded onto the PAGE variable.
        buttons: window.PAGE.buttons.map(button => ({
          text: button.name,
          action() {
            downloadItem({format: button.format});
          }
        }))
      }
    ],
    processing: true,
    serverSide: true,
    ajax: window.PAGE.urls.projects,
    order: [[COLUMNS.MODIFIED_DATE, "desc"]],
    columnDefs: [
      {
        targets: [COLUMNS.NAME],
        render(data, type, full) {
          return createItemLink({
            url: `${window.PAGE.urls.project}${full.id}`,
            label: data
          });
        }
      },
      {
        targets: [COLUMNS.CREATED_DATE, COLUMNS.MODIFIED_DATE],
        render(data) {
          return formatDateDOM({data});
        }
      }
    ],
    createdRow: function(row) {
      const $row = $(row);
      $row.tooltip({selector: "[data-toggle=\"tooltip\"]"});
    }
  });
}
