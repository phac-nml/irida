import "../../vendor/datatables/datatables";
import "../../vendor/datatables/datatables-buttons";
import $ from "jquery";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig,
  wrapCellContents
} from "./../../utilities/datatables-utilities";
import { formatDate } from "./../../utilities/date-utilities";

/*
Get the table headers and create a look up table for them.
This give the row name in snake case and its index.
 */
const COLUMNS = generateColumnOrderInfo();

/**
 * Download table in specified format.
 * @param {string} format format of downloaded doc.
 */
function downloadItem({ format = "xlsx" }) {
  const url = `${window.PAGE.urls.export}&dtf=${format}`;
  const anchor = document.createElement("a");
  anchor.style.display = "none";
  anchor.href = url;
  document.body.appendChild(anchor);
  anchor.click();
  document.body.removeChild(anchor);
}

const config = Object.assign(tableConfig, {
  ajax: window.PAGE.urls.projects,
  // These are loaded through the PAGE object.
  buttons: [
    {
      extend: "collection",
      className: "btn-sm",
      text() {
        return document.querySelector("#export-btn-text").innerHTML;
      },
      // The buttons are loaded onto the PAGE variable.
      // These are for exporting the table to either
      // csv or excel.
      buttons: window.PAGE.buttons.map(button => ({
        text: button.name,
        action() {
          downloadItem({ format: button.format });
        }
      }))
    }
  ],
  order: [[COLUMNS.MODIFIED_DATE, "desc"]],
  columnDefs: [
    {
      targets: [COLUMNS.NAME],
      render(data, type, full) {
        // Render the name as a link to the actual project.
        return createItemLink({
          url: `${window.PAGE.urls.project}${full.id}`,
          label: `${
            full.remote
              ? `<div aria-hidden="true" data-toggle="tooltip" data-placement="top" title="${i18n(
                  "projects.table.remoteSynchronized"
                )}">${data}&nbsp;<i style="color: #000;" class="fas fa-exchange-alt pull-right"></i></div>`
              : data
          }`,
          width: "200px"
        });
      }
    },
    {
      targets: COLUMNS.ORGANISM,
      render(data) {
        return wrapCellContents({ text: data });
      }
    },
    // Format all dates to standate date for the systme.
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
