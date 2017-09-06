import "DataTables/datatables";
//import "DataTables/datatables-buttons";
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
const COLUMNS = generateColumnOrderInfo("#projects");

const config = Object.assign(tableConfig, {
  ajax: window.PAGE.urls.projects,
  searching: false,
  order: [[COLUMNS.MODIFIED_DATE, "desc"]],
  initComplete: function(settings, json) {
    $("#project-count").text(json.recordsTotal);
  },
  columnDefs: [
    {
      targets: [COLUMNS.NAME],
      render(data, type, full) {
        // Render the name as a link to the actual project.
        return createItemLink({
          url: `${window.PAGE.urls.project}${full.id}`,
          label: `${full.remote
            ? `<div aria-hidden="true" data-toggle="tooltip" data-placement="top" title="${window
                .PAGE.i18n
                .remote}">${data}&nbsp;<i style="color: #000;" class="fa fa-exchange pull-right"></i></div>`
            : data}`,
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
