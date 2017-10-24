import "../../../css/pages/analyses-list.css";
import "../../vendor/datatables/datatables";
import $ from "jquery";
import {
  createButtonCell,
  createDeleteBtn,
  createDownloadLink,
  createFilterTag,
  createItemLink,
  createRestrictedWidthContent,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import {
  formatDate,
  getHumanizedDuration
} from "../../utilities/date-utilities";
import { deleteAnalysis } from "../analysis/analysis-service";

/*
Get the table headers and create a look up table for them.
This give the row name in snake case and its index.
 */
const COLUMNS = generateColumnOrderInfo();

/**
 * Create the state cell for the table.  This includes both the
 * state label and the percentage bar.
 * @param {object} full data for row object.
 * @return {string} of DOM representing cell.
 */
function createState(full) {
  const stateClasses = {
    COMPLETED: "progress-bar-success",
    ERROR: "progress-bar-danger"
  };

  let stateClass = "";
  if (stateClasses[full.state] !== null) {
    stateClass = stateClasses[full.state];
  }

  let percent = full.percentComplete;
  if (full.state === "ERROR") {
    percent = 100;
  }
  return `
${full.state}
<div class='progress analysis__state'>
  <div class='progress-bar ${stateClass}' 
       role='progressbar' aria-valuenow='${percent}' 
       aria-valuemin='0' aria-valuemax='100' 
       style='width:${percent}%;'>
  </div>
</div>`;
}

const config = Object.assign(tableConfig, {
  ajax: window.PAGE.URLS.analyses,
  order: [[COLUMNS.CREATED_DATE, "desc"]],
  // Define how the columns should be rendered.
  columnDefs: [
    // Analysis state needs to be displayed with progress bars.
    {
      targets: [COLUMNS.STATE],
      render(data, type, full) {
        return createState(full);
      }
    },
    // Use the analysis name column as a link the the full analysis.
    {
      targets: COLUMNS.NAME,
      render(data, type, full) {
        return createItemLink({
          url: `${window.PAGE.URLS.analysis}${full.id}`,
          label: data
        });
      }
    },
    // Fork flow name are too long and will not fit properly into the column.
    // Restrict the cell width.  This adds a tooltip automatically.
    {
      targets: COLUMNS.WORKFLOW,
      render(data) {
        return createRestrictedWidthContent({ text: data }).outerHTML;
      }
    },
    // Dates need to all be formatted properly.
    {
      targets: [COLUMNS.CREATED_DATE],
      render(data) {
        const date = formatDate({ date: data });
        return `<time>${date}</time>`;
      }
    },
    {
      targets: [COLUMNS.DURATION],
      render(data) {
        return getHumanizedDuration({ date: data });
      }
    },
    {
      targets: COLUMNS.BUTTONS,
      sortable: false,
      width: 50,
      render(data, type, full) {
        const buttons = [];
        // If the submission is completed, then it can be downloaded, created a link
        // to download it.
        if (full.state.localeCompare("Completed") === 0) {
          const anchor = createDownloadLink({
            url: `${window.PAGE.URLS.download}${full.id}`,
            title: `${full.name}.zip`
          });
          buttons.push(anchor);
        }
        // If the user has permission to delete the submission add a button to delete it.
        if (full.updatePermission) {
          const removeBtn = createDeleteBtn({
            id: full.id,
            name: full.name,
            toggle: "modal",
            target: "#deleteConfirmModal" // Id for the modal to confirm the deletion.
          });
          buttons.push(removeBtn);
        }
        return createButtonCell(buttons);
      }
    }
  ]
});
/*
Initialize the DataTable
 */
const table = $("#analyses").DataTable(config);

/**
 * Set the state for the Analyses table filters.
 * @param {string} name of the analysis
 * @param {string} state of the workflow
 * @param {string} workflow identifier
 */
function setFilterState(name, state, workflow) {
  // WORKFLOW: Need to get the internationalized value
  const workflowValue = () => {
    const filter = document.querySelector("#workflowIdFilter");
    const index = filter.selectedIndex;
    if (index > 0) {
      return filter.options[index].text;
    }
    return "";
  };
  const workflowColumn = table.column(COLUMNS.WORKFLOW);
  workflowColumn.search(workflow);
  createFilterTag({
    text: workflowValue(),
    type: workflowColumn.header().innerText,
    handler() {
      workflowColumn.search("").draw();
    }
  });

  // STATE: Need to get the internationalized value
  const stateValue = () => {
    const stateFilter = document.querySelector("#analysisStateFilter");
    const index = stateFilter.selectedIndex;
    if (index > 0) {
      return stateFilter.options[index].text;
    }
    return "";
  };
  const stateColumn = table.column(COLUMNS.STATE);
  stateColumn.search(state);
  createFilterTag({
    text: stateValue(),
    type: stateColumn.header().innerText,
    handler() {
      stateColumn.search("").draw();
    }
  });

  // NAME
  const nameColumn = table.column(COLUMNS.NAME);
  nameColumn.search(name);
  createFilterTag({
    text: name,
    type: nameColumn.header().innerText,
    handler() {
      nameColumn.search("").draw();
    }
  });

  // Get the filtered data
  table.draw(false);
}

// Set up the delete modal
$("#deleteConfirmModal")
  .on("shown.bs.modal", () => {
    document.querySelector("#delete-analysis-button").focus();
  })
  .on("show.bs.modal", function(e) {
    const button = $(e.relatedTarget); // The button that triggered the modal.

    $(this).find("#delete-analysis-button").off("click").on("click", () => {
      deleteAnalysis({ id: button.data("id") }).then(
        result => {
          window.notifications.show({ msg: result.result });
          table.ajax.reload();
        },
        () => {
          window.notifications.show({
            msg: window.PAGE.i18n.unexpectedDeleteError,
            type: "error"
          });
        }
      );
    });
  });

// Set up clear filters button
document.querySelector("#clear-filters").addEventListener("click", () => {
  table.search("");
  setFilterState("", "", "");
});

// Set up the filter modal
const nameFilter = document.querySelector("#nameFilter");
const stateFilter = document.querySelector("#analysisStateFilter");
const workflowFilter = document.querySelector("#workflowIdFilter");
$("#filterModal")
  .on("shown.bs.modal", () => {
    nameFilter.value = table.column(COLUMNS.NAME).search();
    stateFilter.value = table.column(COLUMNS.ANALYSIS_STATE).search();
    workflowFilter.value = table.column(COLUMNS.WORKFLOW_ID).search();
    nameFilter.focus();
  })
  .on("show.bs.modal", function() {
    // When the filter modal is opened, set up the click
    // handlers for all filter properties.
    $(this).find("#filterAnalysesBtn").off("click").on("click", () => {
      const name = nameFilter.value;
      const state = stateFilter.value;
      const workflow = workflowFilter.value;
      setFilterState(name, state, workflow);
    });
  });

/**
 * Generate the advanced filter buttons after the DataTables
 * has been initialized.
 */
(function createFilterButton() {
  const $wrapper = $("#filterBtnWrapper");
  const $btn = $wrapper.find(".btn-toolbar");
  $(document).remove($wrapper);

  // Adjust the default search field;
  $("#analyses_filter").parent().append($btn);
})();
