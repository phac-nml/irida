import $ from "jquery";
import "../../vendor/datatables/datatables";
import {
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import { formatDate } from "../../utilities/date-utilities";
import {
  CONNECT_MODAL_SELECTOR,
  initConnectRemoteApi,
  updateRemoteConnectionStatus
} from "./remote-apis";

const COLUMNS = generateColumnOrderInfo();
const $table = $("#remoteapiTable");

function generateRowId(apiId) {
  return `api-${apiId}`;
}

const config = Object.assign({}, tableConfig, {
  ajax: $table.data("url"),
  order: [[COLUMNS.CREATED_DATE, "desc"]],
  columnDefs: [
    {
      targets: COLUMNS.NAME,
      render(data, type, full) {
        return `<a class="btn btn-link t-api-name" href="${
          window.TL.BASE_URL
        }remote_api/${full.id}">${data}</a>`;
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
      targets: [COLUMNS.STATUS],
      sortable: false,
      render(data, type, full) {
        return `<div class="status-wrapper"><i class="fa fa-spinner fa-pulse spaced-right__sm fa-fw"></i><span data-api-id='${
          full.id
        }' class='connection-status' id=${full.id}>${
          window.PAGE.lang.statusText
        }</span></div>`;
      }
    },
    {
      targets: [COLUMNS.CONNECTION_BUTTON],
      render(data, type, full) {
        return `<button class='oauth-connect-link btn btn-default pull-right hidden' data-toggle="modal" data-target="#${CONNECT_MODAL_SELECTOR}" data-api-id='${
          full.id
        }'>${window.PAGE.lang.connectText}</a>`;
      }
    }
  ],
  createdRow(row, data) {
    const $row = $(row);
    $row.attr("id", generateRowId(data.id));
    updateRemoteConnectionStatus($(row), data.id);
  }
});

$table.DataTable(config);
// Set up the button section
const $btnWrapper = $("#remoteapiTable_wrapper .buttons");
const $toolbar = $(".js-connection-toolbar").detach();
$toolbar.removeClass("hidden");
$btnWrapper.html($toolbar);

initConnectRemoteApi(function(apiId) {
  const $row = $(`#${generateRowId(apiId)}`);
  updateRemoteConnectionStatus($row, apiId);
});
