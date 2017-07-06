import "DataTables/datatables";
import "DataTables/datatables-buttons";
import $ from "jquery";
import {
  createItemLink,
  generateColumnOrderInfo,
  createRestrictedWidthContent,
  tableConfig
} from "Utilities/datatables-utilities";
import { formatDate } from "Utilities/date-utilities";

// Column look-ups for quick referencing
const COLUMNS = generateColumnOrderInfo();

/**
 * Download table in specified format.f
 * @param {string} format format of downloaded doc.
 */
function downloadItem({ format = "xlsx" }) {
  const url = `${window.PAGE.urls.export}&dtf=${format}`;
  const anchor = document.createElement("a");
  anchor.style.display = "none";
  anchor.href = url;
  anchor.click();
}

const config = Object.assign(tableConfig, {
  // These are loaded through the PAGE object.
  buttons: [
    {
      extend: "collection",
      className: "btn-sm",
      text() {
        return document.querySelector("#export-btn-text").innerHTML;
      },
      // The buttons are loaded onto the PAGE variable.
      buttons: window.PAGE.buttons.map(button => ({
        text: button.name,
        action() {
          downloadItem({ format: button.format });
        }
      }))
    }
  ],
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
      targets: COLUMNS.ORGANISM,
      render(data) {
        return createRestrictedWidthContent({
          text: data,
          width: 250
        }).outerHTML;
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE, COLUMNS.MODIFIED_DATE],
      render(data) {
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      }
    }
  ]
});

$("#projects").DataTable(config);
