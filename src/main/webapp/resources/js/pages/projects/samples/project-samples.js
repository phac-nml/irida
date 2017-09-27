import $ from "jquery";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../../utilities/datatables-utilities";
import { formatDate } from "../../../utilities/date-utilities";
import { addSamplesToCart } from "../../../modules/cart/cart";
import "./../../../vendor/datatables/datatables";
import "./../../../vendor/datatables/datatables-buttons";
import "./../../../vendor/datatables/datatables-rowSelection";

/**
 *  Get the names and order of the table columns
 * @type {Object}
 */
const COLUMNS = generateColumnOrderInfo();
/**
 * Get a handle on the table
 * @type {*|jQuery|HTMLElement}
 */
const $table = $("#samplesTable");
/**
 * Get access the the url for the tables data.
 * @type {string}
 */
const url = $table.data("url");

const config = Object.assign({}, tableConfig, {
  ajax: url,
  stateSave: true,
  deferRender: true,
  select: {
    allUrl: window.PAGE.urls.samples.sampleIds,
    formatSelectAllResponseFn(response) {
      // This is a callback function used by datatables-select
      // to format the server response when selectAll is clicked.
      // It puts the response into the format of the `data-info` attribute
      // set on the row itself ({row_id: {projectId, sampleId}}
      const projectIds = Object.keys(response);
      const complete = new Map();
      for (const pId of projectIds) {
        for (const sId of response[pId]) {
          complete.set(`row_${sId}`, {
            project: pId,
            sample: sId
          });
        }
      }
      return complete;
    }
  },
  order: [[COLUMNS.MODIFIED_DATE, "asc"]],
  rowId: "DT_RowId",
  buttons: ["selectAll", "selectNone"],
  language: {
    select: window.PAGE.i18n.select,
    buttons: {
      selectAll: window.PAGE.i18n.buttons.selectAll,
      selectNone: window.PAGE.i18n.buttons.selectNone
    }
  },
  columnDefs: [
    // Add an empty checkbox to the first column in each row
    // This will handle row selection.
    {
      orderable: false,
      data: null,
      render() {
        return `<input type="checkbox"/>`;
      },
      targets: 0
    },
    {
      targets: [COLUMNS.SAMPLE_NAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.TL
            .BASE_URL}projects/${full.projectId}/samples/${full.id}`,
          label: full.sampleName
        });
      }
    },
    {
      targets: [COLUMNS.PROJECT_NAME],
      render(data, type, full) {
        return createItemLink({
          url: `${window.TL.BASE_URL}projects/${full.projectId}`,
          label: data
        });
      }
    },
    {
      targets: [COLUMNS.CREATED_DATE, COLUMNS.MODIFIED_DATE],
      render(data) {
        return `<time>${formatDate({ date: data })}</time>`;
      }
    }
  ],
  createdRow(row, data) {
    row.dataset.info = JSON.stringify({
      project: data.projectId,
      sample: data.id
    });
  }
});

const $dt = $table.DataTable(config);

/*
CART FUNCTIONALITY
 */
const $cartBtn = $("#cart-add-btn");
$cartBtn.on("click", function() {
  const selected = $dt.select.selected()[0];
  /*
  Selected data needs to be formatted into an object: {projectId => [sampleIds]}
   */
  const projects = {};
  selected.forEach(item => {
    projects[item.project] = projects[item.project] || [];
    projects[item.project].push(item.sample);
  });
  addSamplesToCart(projects);
});

/*
TABLE EVENT HANDLERS
 */

// Row selection events.
$dt.on("selection-count.dt", function(e, count) {
  /*
  Update the state of the cart button.
  If there is nothing selected, disable the button.
   */
  $cartBtn.prop("disabled", count === 0);
});
