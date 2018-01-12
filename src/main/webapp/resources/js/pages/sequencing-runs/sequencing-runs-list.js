import "../../vendor/datatables/datatables";
import $ from "jquery";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";

/*
Get the table headers and create a look up table for them.
This give the row name in snake case and its index.
 */
const COLUMNS = generateColumnOrderInfo();
const $table = $("#sequencingRuns");
const url = $table.data("url");

const config = Object.assign({}, tableConfig, {
  ajax: url,
  // Sort by the date with the newest on top.
  order: [[COLUMNS.CREATED_DATE, "desc"]],
  searching: false,
  columnDefs: [
    {
      targets: [COLUMNS.ID],
      render(data) {
        // Render the id column as the id with a link to the acutal
        // Run page.
        return createItemLink({
          url: `${PAGE.urls.link}${data}`,
          label: data,
          width: "100px",
          classes: ["run-link"] // Special class for selenium testing
        });
      }
    },
    {
      targets: [COLUMNS.USER],
      render(data) {
        if (data !== null) {
          return createItemLink({
            // Link to the users page.
            url: `${PAGE.urls.users}${data.identifier}`,
            label: data.label
          });
        }
        return "";
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE],
      render(data) {
        // Render as default date for DataTables on the platform
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      }
    }
  ]
});

$table.DataTable(config);
