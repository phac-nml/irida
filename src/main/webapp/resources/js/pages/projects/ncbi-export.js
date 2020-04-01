import $ from "jquery";
import "../../vendor/datatables/datatables";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";

// Generate the current columns order
const COLUMNS = generateColumnOrderInfo();

// Same dom as normal, but without the search.
// Since this is how it was before I update - may want search in future?
const dom = `
<"dt-table-wrapper"rt>
<"row"
  <"col-md-3 col-sm-12"l>
  <" col-md-6 col-sm-12"p><"col-md-3 col-sm-12 text-right"i>>`;

const $table = $("#exportTable");
const CONFIG = Object.assign({}, tableConfig, {
  dom,
  ajax: $table.data("url"),
  paging: PAGE.paging,
  columnDefs: [
    {
      targets: [COLUMNS.ID],
      render(data, type, full) {
        // Create a link back to the submission.
        const url = `${window.PAGE.URLS.projectBase}${full.project.identifier}/export/${data}`;
        return createItemLink({
          url,
          label: data,
          width: 50
        });
      }
    },
    {
      targets: [COLUMNS.PROJECT],
      render(data, type, full) {
        // Create a link back to the project
        const url = `${PAGE.URLS.projectBase}${full.project.identifier}`;
        return createItemLink({
          url,
          label: data.name,
          width: 300
        });
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE],
      render(data) {
        // Format the time to the standard
        return `<time>${formatDate({ date: data })}</time>`;
      }
    }
  ]
});

$table.DataTable(CONFIG);
