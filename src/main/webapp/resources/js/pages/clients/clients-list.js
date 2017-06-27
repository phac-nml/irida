import "DataTables/datatables";
import $ from "jquery";
import {activateTooltips, createItemLink, dom, generateColumnOrderInfo} from "Utilities/datatables-utilities";
import {formatDateDOM} from "Utilities/date-utilities";

const COLUMNS = generateColumnOrderInfo();

const $table = $("#clientsTable");

$table.DataTable({
  dom,
  processing: true,
  serverSide: true,
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
          url: `${$table.data("clients")}${row[0]}`
        });
      },
      targets: COLUMNS.CLIENT_ID
    },
    {
      targets: COLUMNS.CREATED_DATE,
      render(data) {
        return formatDateDOM({data});
      }
    }
  ],
  createdRow(row) {
    activateTooltips(row);
  }
});

$(document).ready(() => {
  const $addLink = $('#add-link');
  $('.buttons').append($addLink);
  $addLink.removeClass("hidden");
});
