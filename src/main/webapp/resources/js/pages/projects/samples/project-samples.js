import $ from "jquery";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../../utilities/datatables-utilities";
import { formatDate } from "../../../utilities/date-utilities";
import "./../../../vendor/datatables/datatables";
import "./../../../vendor/datatables/datatables-buttons";
import "./../../../vendor/datatables/datatables-rowSelection";

const COLUMNS = generateColumnOrderInfo();
const $table = $("#project-samples");
const url = $table.data("url");

const config = Object.assign({}, tableConfig, {
  ajax: url,
  stateSave: true,
  deferRender: true,
  select: {
    allUrl: window.PAGE.urls.samples.sampleIds,
    formatSelectAllResponseFn(response) {
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

$dt.on("selection-count.dt", function(e, count) {
  const selected = $dt.select.selected()[0];
});
