import "../../../sass/pages/analyses-list.scss";
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
import { showNotification } from "../../modules/notifications";

/*
Get the table headers and create a look up table for them.
This give the row name in snake case and its index.
 */
const COLUMNS = generateColumnOrderInfo();

/*
Defaults for table popovers
 */
const POPOVER_OPTIONS = {
  animation: true,
  container: "body",
  trigger: "click",
  placement: "auto right",
  html: true,
  template: $("#popover-template").clone()
};

const SHOW_POPOVER = `[preview]`;
const HIDE_POPOVER = `[hide]`;

const CHAR_TO_ESCAPED = {
  "&": "&amp;",
  "<": "&lt;",
  ">": "&gt;",
  '"': "&quot;",
  "'": "&#39;",
  "/": "&#x2F;",
  "`": "&#x60;",
  "=": "&#x3D;"
};
/**
 * Replace special characters in a string with escaped characters
 * @param string String to escape
 * @returns {string} Escaped string
 */
function escapeHtml(string) {
  return String(string).replace(/[&<>"'`=\/]/g, s => CHAR_TO_ESCAPED[s]);

}

/**
 * Truncate a string to a specified length prepending "..." if truncated
 * @param s String to truncate
 * @param length Truncate string to an integer `length`
 * @returns {string} Truncated string `s` if string length longer than `length`
 */
const truncString = (s, length) => {
  return s.length > length ? "..." + s.substring(s.length - length) : s;
};

/**
 * Create the state cell for the table.  This includes both the
 * state label and the percentage bar.
 * If there was a Galaxy Job error then allow user to preview that information,
 * in a popover overlay.
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

  let errorStateClass = "";
  let percent = full.percentComplete;
  if (/^Error.*/.test(full.state)) {
    stateClass = stateClasses.ERROR;
    percent = 100;
    errorStateClass = "class='js-analysis-error'";
  }
  let showingPopover = "";
  if (typeof full.jobError !== "undefined" && full.jobError !== null) {
    showingPopover = `<br>
        <span class="js-showing-popover small text-muted">
            ${SHOW_POPOVER}
        </span>`;
  }
  return `
<div ${errorStateClass}>
  ${full.state}
  ${showingPopover}
  <div class='progress analysis__state'>
    <div class='progress-bar ${stateClass}' 
         role='progressbar' aria-valuenow='${percent}' 
         aria-valuemin='0' aria-valuemax='100' 
         style='width:${percent}%;'>
    </div>
  </div>
</div>
`;
}

/**
 * Get a handle on the table
 * @type {*|jQuery|HTMLElement}
 */
const $table = $("#analyses");

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
  ],
  createdRow(row, data) {
    if (
      typeof data.jobError === "undefined" ||
      data.jobError === null ||
      $.isEmptyObject(data.jobError)
    ) {
      return;
    }
    const joberror = data.jobError;
    const $row = $(row);
    const $errorTd = $row.find(".js-analysis-error");
    if ($errorTd.length > 0) {
      const $link = $row.find("a.btn-link");
      const MAX_LENGTH = 400;
      let stderr = joberror.standardError;
      let stdout = joberror.standardOutput;

      stderr = truncString(stderr, MAX_LENGTH);
      stdout = truncString(stdout, MAX_LENGTH);
      const content = `
<div>
  <h5>Standard Error</h5>
  <pre>${escapeHtml(stderr)}</pre>
  <h5>Standard Output</h5>
  <pre>${escapeHtml(stdout)}</pre>
  <p>Go to <a href="${$link.attr("href")}">${$link.html()}</a> for more info</p>
</div>`;
      const title = `
<span>
  Job Error - ${joberror.toolName} (v${joberror.toolVersion}) 
</span>
<i class="pull-right fa fa-fw fa-times text-danger js-close-popover" />
`;
      $errorTd.data("title", title);
      $errorTd.data("content", content);
      $errorTd.css("position", "relative");
      $errorTd.css("cursor", "pointer");

      $errorTd
        .popover(POPOVER_OPTIONS)
        .on("shown.bs.popover", () => {
          $errorTd.find(".js-showing-popover").html(HIDE_POPOVER);
          $(".popover").css(
            "max-width",
            Math.floor($(window).width() * 0.5) + "px"
          );
          $(".js-close-popover").on("click", () => {
            $errorTd.popover("hide");
            $errorTd.popover(POPOVER_OPTIONS);
          });
        })
        .on("hidden.bs.popover", e => {
          $errorTd.find(".js-showing-popover").html(SHOW_POPOVER);
          // need following to ensure that user does not need to click twice
          // to open popover (see https://stackoverflow.com/a/34320956)
          $(e.target).data("bs.popover").inState.click = false;
        });
    }
  }
});
/*
Initialize the DataTable
 */
const table = $table.DataTable(config);

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

    $(this)
      .find("#delete-analysis-button")
      .off("click")
      .on("click", () => {
        deleteAnalysis({ id: button.data("id") }).then(
          result => {
            showNotification({ text: result.result });
            table.ajax.reload();
          },
          () => {
            showNotification({
              text: window.PAGE.i18n.unexpectedDeleteError,
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
    $(this)
      .find("#filterAnalysesBtn")
      .off("click")
      .on("click", () => {
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
  $("#analyses_filter")
    .parent()
    .append($btn);
})();
