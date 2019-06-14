import "../../../sass/pages/analyses-list.scss";
import "../../vendor/datatables/datatables";
import $ from "jquery";
import {
  createButtonCell,
  createDeleteBtn,
  createDownloadLink,
  createFilterTag,
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../utilities/datatables-utilities";
import {
  formatDate,
  getHumanizedDuration
} from "../../utilities/date-utilities";
import { deleteAnalysis } from "../analysis/analysis-service";
import { showNotification } from "../../modules/notifications";
import { escapeHtml } from "../../utilities/html-utilities";

/**
 * Internationalized messages
 * @type {Object} map of messages key name to i18n text
 */
let I18N = {
  "analysis.details.joberror.standardOutput": "STDOUT",
  "analysis.details.joberror.standardError": "STDERR",
  "analysis.joberror.popover.goto-submission": "GOTO SUBMISSION {0}",
  "analysis.joberror": "JOBERROR",
  "analysis.joberror.popover.truncated-output": "TRUNCATED",
  "analysis.joberror.popover.click-to-show": "CLICK IT"
};
I18N = Object.assign(I18N, window.PAGE.i18n);

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
  sanitize: false,
  template: $("#popover-template").clone()
};

const jobErrorIcon = `
<i class="fa fa-fw fa-question-circle js-job-error-tooltip" 
   data-toggle="tooltip"
   data-placement="auto right"
   title="${I18N["analysis.joberror.popover.click-to-show"]}">
</i>`;

/**
 * Get a handle on the table
 * @type {*|jQuery|HTMLElement}
 */
const $table = $("#analyses");

/**
 * Truncate a multiline string `s` to only the last `n` lines.
 * @param s Multiline string to truncate if necessary
 * @param n Number of lines to take from end of `s`
 * @param delimiter New-line delimiter
 * @returns {String} Truncated multiline string
 */
const truncateOutput = (s, n = 5, delimiter = "\n") => {
  const split = s.split(delimiter);
  if (split.length <= n) {
    return s;
  }
  const out = split
    .slice(split.length - n - 1, split.length)
    .join(delimiter)
    .trim();
  return `[...${I18N["analysis.joberror.popover.truncated-output"]}...]
${out}`;
};

/**
 * Create the state cell for the table.  This includes both the
 * state label and the percentage bar.
 * If there was a Galaxy Job error then allow user to preview that information,
 * in a popover overlay.
 * @param {object} full data for row object.
 * @return {string} of DOM representing cell.
 */
function createState({ state, percentComplete, jobError }) {
  const stateClasses = {
    COMPLETED: "progress-bar-success",
    ERROR: "progress-bar-danger"
  };
  let stateClass = "";
  if (stateClasses[state] !== null) {
    stateClass = stateClasses[state];
  }
  let errorStateClass = "";
  let percent = percentComplete;
  if (/^Error.*/.test(state)) {
    stateClass = stateClasses.ERROR;
    percent = 100;
    errorStateClass = "class='js-analysis-error'";
  }
  return `
<div ${errorStateClass}>
  ${state}
  ${jobError !== null ? jobErrorIcon : ""}
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
 * Check for clicking outside of a popover. Close any popovers if user clicks
 * outside the popover. Remove this event handler from $("body")
 * @param e
 */
const clickAwayPopoverHandler = e => {
  const $target = $(e.target);
  if (
    $target.data("toggle") !== "popover" &&
    $target.parents(".popover.in").length === 0
  ) {
    $("[data-original-title]").popover("hide");
    // remove this event handler
    unregisterPopoverClickAway();
  }
};

/**
 * Document body HTML element
 * @type {jQuery|HTMLElement}
 */
const $body = $("body");
/**
 * Unregister `clickAwayPopoverHandler` event from `$body`
 * @returns {*}
 */
const unregisterPopoverClickAway = () =>
  $body.off("click", clickAwayPopoverHandler);

/**
 * Register `clickAwayPopoverHandler` event to `$body`
 * @returns {*}
 */
const registerPopoverClickAway = () =>
  $body.on("click", clickAwayPopoverHandler);

/**
 * Setup the BS3 Popover UI for showing job error info
 * @param jobError Destructured job error object
 * @param row DataTables row
 */
const setupJobErrorPopoverUI = ({ jobError }, row) => {
  const $row = $(row);
  const $errorTd = $row.find(".js-analysis-error");
  if ($errorTd.length > 0 && jobError !== null) {
    const $link = $row.find("a.btn-link");
    let { standardError = "", standardOutput = "" } = jobError;
    // only show the last N lines of stderr/stdout
    standardError = truncateOutput(standardError);
    standardOutput = truncateOutput(standardOutput);
    // construct <a> link to analysis submission details page
    const linkToSubmission = `<a href="${$link.attr(
      "href"
    )}">${$link.html()}</a>`;
    // text and link for "Go to {analysis submission page} for more info"
    const goto = I18N["analysis.joberror.popover.goto-submission"].replace(
      "{0}",
      linkToSubmission
    );
    // popover main content
    const content = `
<div>
  <h5>${I18N["analysis.details.joberror.standardError"]}</h5>
  <pre>${escapeHtml(standardError)}</pre>
  <h5>${I18N["analysis.details.joberror.standardOutput"]}</h5>
  <pre>${escapeHtml(standardOutput)}</pre>
  <p>${goto}</p>
</div>`;
    const title = `
<span>
  ${I18N["analysis.joberror"]} - ${jobError.toolName} (v${jobError.toolVersion})
</span>
<i class="pull-right fa fa-fw fa-times text-danger js-close-popover" />
`;
    // set popover data attrs
    $errorTd.data({ title: title, content: content });
    $errorTd.css({ position: "relative", cursor: "pointer" });

    // initialize popover for table cell with job error info
    $errorTd
      .popover(POPOVER_OPTIONS)
      .on("shown.bs.popover", () => {
        // adjust width of popover to X% of client window width
        $(".popover").css(
          "max-width",
          Math.floor($(window).width() * 0.33) + "px"
        );
        // popover close button click event
        $(".js-close-popover").on("click", () => {
          $errorTd.popover("hide");
          $errorTd.popover(POPOVER_OPTIONS);
        });
        // register event for when user clicks outside of popover to dismiss it
        registerPopoverClickAway();
      })
      .on("hidden.bs.popover", e => {
        // need following to ensure that user does not need to click twice
        // to open popover (see https://stackoverflow.com/a/34320956)
        $(e.target).data("bs.popover").inState.click = false;
      });
  }
};

/**
 * Initialize tooltip for icon that says "Click to preview job error info".
 * Hide on click to show popover or when popover is already open.
 * @param row DataTables row
 */
function initClickToShowJobErrorTooltip(row) {
  const $el = $(row).find(".js-job-error-tooltip");
  $el
    .tooltip({ container: "body", trigger: "manual" })
    .on("click", () => $el.tooltip("hide"))
    .on("mouseenter", () => {
      // popover template will have `.popover` class
      if ($(".popover").length === 1) {
        $el.tooltip("show");
      } else {
        $el.tooltip("hide");
      }
    })
    .on("mouseleave", () => $el.tooltip("hide"));
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
          label: data,
          width: undefined
        });
      }
    },
    // Fork flow name are too long and will not fit properly into the column.
    // Restrict the cell width.  This adds a tooltip automatically.
    {
      targets: COLUMNS.WORKFLOW,
      render(data) {
        return data;
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
    setupJobErrorPopoverUI(data, row);
    initClickToShowJobErrorTooltip(row);
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
