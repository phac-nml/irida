import $ from "jquery";
import chroma from "chroma-js";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig
} from "../../../utilities/datatables-utilities";
import { formatDate } from "../../../utilities/date-utilities";
import "./../../../vendor/datatables/datatables";
import "./../../../vendor/datatables/datatables-buttons";
import "./../../../vendor/datatables/datatables-rowSelection";
import { CART } from "../../../utilities/events-utilities";
import SampleDropdownButton from "./SampleDropdownButton";

/*
 Initialize the sample tools menu.
 */
const SAMPLE_TOOL_ACTIONS = {
  "cart-add-btn"() {
    const selected = $dt.select.selected()[0];
    /*
    Selected data needs to be formatted into an object: {projectId => [sampleIds]}
     */
    const projects = {};
    selected.forEach(item => {
      projects[item.project] = projects[item.project] || [];
      projects[item.project].push(item.sample);
    });

    /*
    Update the cart with the new samples.
     */
    const event = new CustomEvent(CART.ADD, { detail: { projects } });
    document.dispatchEvent(event);
  }
};

const POPOVER_OPTIONS = {
  container: "body",
  trigger: "hover",
  placement: "right",
  html: true,
  template: $("#popover-template").clone()
};

const sampleToolsNodes = document.querySelectorAll(".sample-tool-btn");
const SAMPLE_TOOL_BUTTONS = [...sampleToolsNodes].map(
  elm => new SampleDropdownButton(elm, SAMPLE_TOOL_ACTIONS[elm.id])
);

/**
 * Reference to the currently selected associated projects.
 * @type {Map}
 */
const ASSOCIATED_PROJECTS = new Map();

/**
 * Reference to sample ids used in filter-by-file.
 * @type {Array}
 */
let FILE_SAMPLE_NAMES = [];

/**
 * Reference to the colour for a specific project.
 * @type {Map}
 */
const PROJECT_COLOURS = (function() {
  const colours = new Map();
  $(".associated-cb input").each((i, elm) => {
    const input = $(elm);
    const colour = chroma.random();
    /*
    Add some colour to the checkbox so it can easily be
    associated with the name in the table
     */
    $(
      `<div class="label-bar-color" style="margin: 0; background-color: ${colour}">&nbsp;</div>`
    ).insertAfter(input);

    colours.set(Number(input.val()), colour);
  });
  return colours;
})();

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
  ajax: {
    url,
    data(d) {
      /*
      Add any extra parameters that need to be passed to the server
      here.
       */
      if (ASSOCIATED_PROJECTS.size > 0) {
        // Add a list of ids for currently visible associated projects
        d.associated = Array.from(ASSOCIATED_PROJECTS.keys());
      }

      /*
      Add any filter by file names that need to be passed.
       */
      if (FILE_SAMPLE_NAMES.length > 0) {
        d.sampleNames = FILE_SAMPLE_NAMES;
      }
    }
  },
  stateSave: true,
  deferRender: true,
  select: {
    allUrl: window.PAGE.urls.samples.sampleIds,
    allPostDataFn() {
      return {
        associated: [...ASSOCIATED_PROJECTS.keys()]
      };
    },
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
  order: [[COLUMNS.MODIFIED_DATE, "desc"]],
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
          label: `<div class="label-bar-color" style="background-color: ${PROJECT_COLOURS.get(
            full.projectId
          )}">&nbsp;</div>${data}`,
          classes: ["project-link"]
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
  drawCallback() {
    $table.find('[data-toggle="popover"]').popover(POPOVER_OPTIONS);
  },
  createdRow(row, data) {
    const $row = $(row);
    row.dataset.info = JSON.stringify({
      project: data.projectId,
      sample: data.id
    });

    /*
    Check to see if row should be selected based on sample name
     */
    if (FILE_SAMPLE_NAMES.includes(data.sampleName)) {
      this.api().select.select(row);
    }

    if (!data.owner) {
      const icon = $(".locked-wrapper").clone();
      const td = $row.find("td:first-of-type");
      td.css("position", "relative");
      icon.appendTo(td);
    }

    /*
    Check if this sample has any quality control issues.
    If there are they will be displayed in a popover.
     */
    if (data.qcEntries.length) {
      $row.addClass("row-warning");

      const icon = $(".qc-warning-wrapper").clone();
      /*
      Generate the content for the popover
       */
      const content = `<ul class="popover-list">
          ${data.qcEntries.map(qc => `<li class="error">${qc}</li>`)}
      </ul>`;
      icon.data("content", content);
      const td = $row.find(`td:nth-of-type(${COLUMNS.SAMPLE_NAME + 1})`);
      td.css("position", "relative");
      icon.appendTo(td);
    }
  }
});

const $dt = $table.DataTable(config);

// This allows for the use of checkboxes in the dropdown without
// it closing on every click.
const ASSOCIATED_INPUTS = $(".associated-cb input");
$(".associated-dd .dropdown-menu a").on("click", function(event) {
  /*
  Find the input element.
   */
  const $inp = $(event.currentTarget).find("input");
  const id = $inp.val();

  /*
  This is a little finicky.  If the user clicked the actual input element,
  then get the checked property of the input.  Else the input has not yet changed
  so get its opposite.
   */
  const checked =
    event.target instanceof HTMLInputElement
      ? $inp.prop("checked")
      : !$inp.prop("checked");

  if (id === "ALL") {
    // Need to get all the ids and select all the checkboxes
    ASSOCIATED_INPUTS.each((index, elm) => {
      const $elm = $(elm);
      $elm.prop("checked", checked);

      if (checked) {
        ASSOCIATED_PROJECTS.set($elm.val(), true);
      } else {
        ASSOCIATED_PROJECTS.delete($elm.val());
      }
    });
  } else {
    if (ASSOCIATED_PROJECTS.has(id)) {
      ASSOCIATED_PROJECTS.delete(id);
    } else {
      ASSOCIATED_PROJECTS.set(id, true);
    }
  }

  setTimeout(function() {
    // Update the current checkbox
    $inp.prop("checked", checked);
    // Update the select all checkbox
    $("#select-all-cb").prop(
      "checked",
      ASSOCIATED_PROJECTS.size === ASSOCIATED_INPUTS.size()
    );
    // Update the DataTable
    $dt.ajax.reload(null, false);
  }, 0);

  $(event.target).blur();
  return false;
});

/*
TABLE EVENT HANDLERS
 */

// Row selection events.
$dt.on("selection-count.dt", function(e, count) {
  /*
  Update the state of the buttons in the navbar.
   */
  for (const btn of SAMPLE_TOOL_BUTTONS) {
    btn.checkState(count);
  }
});

/*
Store reference to handle different modals.
 */
const MODALS = {
  /**
   * Hanle opening the merge modal.
   * @param {string} url for the content of the modal
   * @param {string} src url for the javascript needed for modal.
   * @param {list} sampleIds list of sample ids to merge.
   */
  merge({ url, src, sampleIds }) {
    const modal = $(this);

    /*
    Load the content for the dialogue based on the server response.
     */
    modal.load(`${url}?${$.param({ sampleIds })}`, function() {
      if (typeof src !== "undefined") {
        const script = document.createElement("script");
        script.type = "text/javascript";
        script.src = src;
        document.getElementsByTagName("head")[0].appendChild(script);
      }
    });

    /*
    Handle the closing the modal
     */
    modal.on("samples:merged", function(e) {
      $(this).modal("hide");
      $dt.select.selectNone();
      $dt.ajax.reload();
    });
  }
};

/*
Handle MERGE through the modal window.
 */
$("#modal-wrapper").on("show.bs.modal", function(event) {
  const wrapper = this;
  /*
  Determine which modal to open
   */
  const btn = $(event.relatedTarget);
  const whichModal = btn.data("modal");
  const url = btn.data("url");
  const script = btn.data("script");

  /*
  Find the ids for the currently selected samples.
   */
  const selected = $dt.select.selected()[0];
  const sampleIds = [];
  for (let [key, value] of selected) {
    sampleIds.push(value.sample);
  }

  MODALS[whichModal].call(wrapper, { url, src: script, sampleIds });
});

/*
Add the filter buttons
 */
const filterByFileBtn = $("#filter-toolbar").detach();
filterByFileBtn.appendTo("#dt-filters");

// Set up the file filer
function handleFileSelect(e) {
  const file = e.target.files[0];
  if (typeof file === "undefined") return;
  const reader = new FileReader();
  reader.onload = function(e) {
    // From the file contents, get a list of names (1 per line expected);
    const contents = e.target.result.match(/[^\r\n]+/g);
    // Store the unique values in the ajax variable
    FILE_SAMPLE_NAMES = [...new Set(contents)];
    // Refresh the table.
    $dt.ajax.reload();
  };
  reader.readAsText(file);
}
const filterByFileInput = document.querySelector("#filter-by-file");
filterByFileInput.addEventListener("change", handleFileSelect, false);

/*
Activate page tooltips
 */
$('[data-toggle="popover"]').popover();
