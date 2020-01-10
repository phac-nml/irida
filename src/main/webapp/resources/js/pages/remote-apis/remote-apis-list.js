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
import { setBaseUrl } from "../../utilities/url-utilities";

const COLUMNS = generateColumnOrderInfo();
const $table = $("#remoteapiTable");

/**
 * each row in the datatable needs a unique id, this generate an id based
 * on the identifier for that API.
 * @param {number} apiId the unique identifier for the API
 * @returns {string}
 *
 */
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
        const url = setBaseUrl(`remote_api/${full.id}`);
        return `<a class="btn btn-link t-api-name" href="${url}">${data}</a>`;
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
        return `<div class="js-status-wrapper"><i class="fa fa-spinner fa-pulse spaced-right__sm fa-fw"></i><span data-api-id='${full.id}' class='connection-status' id=${full.id}>${window.PAGE.lang.statusText}</span></div>`;
      }
    },
    {
      targets: [COLUMNS.CONNECTION_BUTTON],
      render(data, type, full) {
        return `<button class='oauth-connect-link btn btn-default pull-right hidden' data-toggle="modal" data-target="#${CONNECT_MODAL_SELECTOR}" data-api-id='${full.id}'>${window.PAGE.lang.connectText}</a>`;
      }
    }
  ],
  createdRow(row, data) {
    const $row = $(row);
    $row.attr("id", generateRowId(data.id));
    updateRemoteConnectionStatus($(row), data.id);
  }
});

/**
 * Initialize the DataTable
 */
$table.DataTable(config);

/**
 * Stet up the "add remote connection" button in the proper place on the datatable.
 */
const $btnWrapper = $("#remoteapiTable_wrapper").find(".buttons");
const $toolbar = $(".js-connection-toolbar").detach();
$toolbar.removeClass("hidden");
$btnWrapper.html($toolbar);

/**
 * Initialize the  connect to remote api functionality.
 */
initConnectRemoteApi(function(apiId) {
  const $row = $(`#${generateRowId(apiId)}`);
  updateRemoteConnectionStatus($row, apiId);
});
