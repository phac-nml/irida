import $ from "jquery";
import { formatDate } from "../../utilities/date-utilities";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import "DataTables/datatables";

/*
Get the table headers and create a look up table for them.
This give the row name in snake case and its index.
 */
const COLUMNS = generateColumnOrderInfo();
const $table = $("#sequencingRuns");
const url = $table.data("url");

const config = Object.assign({}, tableConfig, {
  ajax: url,
  order: [[COLUMNS.CREATED_DATE, "desc"]],
  columnDefs: [
    {
      targets: [COLUMNS.ID],
      render(data, type, full) {
        return createItemLink({
          url: `${PAGE.urls.link}${data}`,
          label: data,
          width: "100px"
        });
      }
    },
    {
      targets: [COLUMNS.USER],
      render(data) {
        if (data !== null) {
          return createItemLink({
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
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      }
    }
  ]
});

$table.DataTable(config);

