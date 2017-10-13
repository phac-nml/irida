import "../../vendor/datatables/datatables";
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
let COLUMNS = generateColumnOrderInfo();

const config = Object.assign(tableConfig, {
  ajax: window.PAGE.urls.table,
  order: [[COLUMNS.USERNAME, "desc"]],
  columnDefs: [
    {
      targets: [COLUMNS.USERNAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.urls.link}${full.id}`,
          label: data
        });
      }
    },
    {
      targets: [COLUMNS.EMAIL],
      render(data) {
        return `<a href="mailto:${data}" class="btn btn-link">${data}</a>`;
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE],
      render(data) {
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      }
    },
    {
      targets: [COLUMNS.LAST_LOGIN],
      render(data) {
        if (data != null) {
          const date = formatDate({ date: data });
          return `<time class="last-login">${date}</time>`;
        } else {
          return "";
        }
      }
    }
  ]
});

$("#usersTable").DataTable(config);
