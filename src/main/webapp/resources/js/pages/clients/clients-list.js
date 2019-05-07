import $ from "jquery";
import "../../vendor/datatables/datatables";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";

const COLUMNS = generateColumnOrderInfo();

const $table = $("#clientsTable");
const config = Object.assign(tableConfig, {
  ajax: $table.data("url"),
  order: [[COLUMNS.CLIENT_ID, "desc"]],
  columnDefs: [
    {
      className: "clientIdCol",
      targets: COLUMNS.ID
    },
    {
      render: function(data, type, row) {
        return createItemLink({
          label: data,
          url: `${$table.data("clients")}${row.id}`
        });
      },
      targets: COLUMNS.CLIENT_ID
    },
    {
      targets: COLUMNS.CREATED_DATE,
      render(data) {
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      }
    }
  ]
});

$table.DataTable(config);

$(document).ready(() => {
  const $addLink = $("#add-link");
  $(".buttons").append($addLink);
  $addLink.removeClass("hidden");
});
