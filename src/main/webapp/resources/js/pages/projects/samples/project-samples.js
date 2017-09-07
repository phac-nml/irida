import "./../../../vendor/datatables/datatables";
import {
  tableConfig,
  generateColumnOrderInfo,
  createItemLink
} from "../../../utilities/datatables-utilities";
import { formatDate } from "../../../utilities/date-utilities";

const COLUMNS = generateColumnOrderInfo();

const $table = $("#project-samples");
const url = $table.data("url");

const state = (function() {
  const selected = [];
  let current;

  function getIndexForRowInfo(info) {
    for (let index = 0; index < selected.length; index++) {
      const indexInfo = selected[index];
      if (
        indexInfo.sample === info.sample &&
        indexInfo.project === info.project
      ) {
        console.log("FOUND");
        return index;
      }
    }
    return -1;
  }

  function displayRowUnselected({ $row }) {
    const checkbox = $row.find("input[type='checkbox']");
    checkbox.prop("checked", false);
    $row.removeClass("selected");
  }

  function displayRowSelected({ $row }) {
    const checkbox = $row.find("input[type='checkbox']");
    checkbox.prop("checked", true);
    $row.addClass("selected");
  }

  function deselectRow({ $row, index }) {
    displayRowUnselected({ $row });
    selected.splice(index, 1);
    current = undefined;
  }

  function selectRow({ $row, info }) {
    displayRowSelected({ $row });
    selected.push(info);
    current = info;
  }

  function selectMultiple({ $row, info }) {
    // Make sure the row clicked gets selected
    displayRowSelected({ $row });

    // Get all the ids currently available on the table.
    const $rows = $("tbody tr");
    let inside = false;
    $rows.each((index, element) => {
      const $r = $(element);
      const elId = $r.data("info");

      if (elId.sample === current.sample || elId.sample === info.sample) {
        inside = !inside;
      } else if (inside) {
        selectRow({ $row: $r, info: elId });
      }
    });
  }

  function toggleRow({ $row, info, event }) {
    const index = getIndexForRowInfo(info);
    const shiftKey = event.shiftKey;

    if (index > -1) {
      deselectRow({ $row, index });
    } else {
      // CHeck for multiple row selection
      if (typeof current !== "undefined" && shiftKey) {
        selectMultiple({ $row, info });
      } else {
        selectRow({ $row, info });
      }
    }
  }

  function checkRow(row) {
    if (selected.length > 0) {
      const $row = $(row);
      const info = $row.data("info");
      console.log(info);
      const index = getIndexForRowInfo(info);
      console.log(index);
      if (index > -1) {
        displayRowSelected({ $row });
      }
    }
  }

  function toggleState({ row, event }) {
    const $row = $(row);
    const info = $row.data("info");
    toggleRow({ $row, info, event });
  }

  function pageChanged() {
    current = undefined;
  }

  return {
    toggleState,
    checkRow,
    pageChanged
  };
})();

const config = Object.assign({}, tableConfig, {
  ajax: url,
  stateSave: true,
  deferRender: true,
  order: [[COLUMNS.MODIFIED_DATE, "asc"]],
  rowId: "DT_RowId",
  columnDefs: [
    {
      targets: 0,
      data: null,
      render() {
        return `<input type="checkbox"/>`;
      }
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
    state.checkRow(row);
  }
});

$table.on("click", "tbody tr", function(event) {
  if (!event.target === "input") {
    event.preventDefault();
  }
  state.toggleState({ row: this, event });
});

$table.DataTable(config).on("page.dt", () => {
  state.pageChanged();
});
