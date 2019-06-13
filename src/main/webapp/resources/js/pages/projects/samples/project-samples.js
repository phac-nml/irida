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
import {
  SampleCartButton,
  SampleDropdownButton,
  SampleExportButton,
  SampleProjectDropdownButton
} from "./SampleButtons";
import { FILTERS, SAMPLE_EVENTS } from "./constants";
import { download } from "../../../utilities/file.utilities";
import moment from "moment";
import "../../../../sass/pages/project-samples.scss";
import { putSampleInCart } from "../../../apis/cart/cart";
import { cartNotification } from "../../../utilities/events-utilities";

/*
This is required to use select2 inside a modal.
This is required to use select2 inside a modal.
 */
$.fn.modal.Constructor.prototype.enforceFocus = function() {};

/*
Defaults for table popovers
 */
const POPOVER_OPTIONS = {
  container: "body",
  trigger: "hover",
  placement: "right",
  sanitize: false,
  html: true,
  template: $("#popover-template").clone()
};

const IS_REMOTE_PROJECT = window.PAGE.isRemoteProject;

/*
 Initialize the sample tools menu.  This is used to check the status of the buttons.
 */
const sampleToolsNodes = document.querySelectorAll(".js-sample-tool-btn");
const SAMPLE_TOOL_BUTTONS = [...sampleToolsNodes].map(
  elm => new SampleDropdownButton(elm)
);

/**
 * Get the ids for all selected samples within the table.
 * @returns {Array}
 */
const getSelectedIds = () => {
  const selected = $dt.select.selected()[0];
  const ids = [];
  for (let [, value] of selected) {
    // Selected samples not currently listed on the page (i.e. from a different page
    // in the table only store minimal data not the full sample.
    ids.push(typeof value.sample === "undefined" ? value.id : value.sample);
  }
  return ids;
};

/*
Initialize the sample export menu.
 */
const EXPORT_HANDLERS = {
  download() {
    // this is set by the object calling (i.e. download btn)
    const url = this.data("url");
    download(`${url}?${$.param({ ids: getSelectedIds() })}`);
  },
  file() {
    // this is set by the object calling (i.e. download btn)
    const url = this.data("url");
    const params = $dt.ajax.params();
    params.type = this.data("file");
    download(`${url}?${$.param(params)}`);
  },
  ncbi() {
    const ids = getSelectedIds();
    /*
    NCBI Export is a separate page.  If there are ids available for export,
    redirect the user to that page.
     */
    if (ids.length > 0) {
      window.location.href = `${this.data("url")}?${$.param({ ids })}`;
    }
  }
};
[...document.querySelectorAll(".js-sample-export-btn")].forEach(elm => {
  if (EXPORT_HANDLERS[elm.dataset.type]) {
    SAMPLE_TOOL_BUTTONS.push(
      new SampleExportButton(elm, EXPORT_HANDLERS[elm.dataset.type])
    );
  } else {
    throw new Error(
      "Sample Export buttons must have a data attribute of 'type'"
    );
  }
});
[...document.querySelectorAll(".js-sample-project-tool-btn")].forEach(elm => {
  SAMPLE_TOOL_BUTTONS.push(
    new SampleProjectDropdownButton(elm, IS_REMOTE_PROJECT)
  );
});

/*
Initialize the add to cart button
 */
const cartBtn = new SampleCartButton($(".js-cart-btn"), function() {
  const projects = {};
  const selected = $dt.select.selected()[0].keys();
  let next = selected.next();
  while (!next.done) {
    const data =
      $dt.row(`#${next.value}`).data() ||
      $dt.select.selected()[0].get(next.value);
    projects[data.projectId] = projects[data.projectId] || [];
    projects[data.projectId].push({ id: data.id, label: data.sampleName });
    next = selected.next();
  }

  // Updated post method
  Object.keys(projects).forEach(id => {
    putSampleInCart(+id, projects[id]).then(response => {
      cartNotification(response.data);
    });
  });
});
SAMPLE_TOOL_BUTTONS.push(cartBtn);

/**
 * Reference to the currently selected associated projects.
 * @type {Map}
 */
const ASSOCIATED_PROJECTS = new Map();

/**
 * Reference to all filters available on the table.
 * @type {Map<any, any>}
 */
const TABLE_FILTERS = new Map();

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
    type: "POST",
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
      Add any available filters
       */
      for (let [key, value] of TABLE_FILTERS) {
        d[key] = value;
      }

      displayFilters.call($dt, TABLE_FILTERS);
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
      const complete = new Map();
      response.forEach(sample => complete.set(`row_${sample.id}`, sample));
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
      render(data, type, full) {
        const checkbox = `<input class="t-row-select" type="checkbox"/>`;

        /**
         * If the project does not have privileges on the sample
         * display a locked symbol.
         */
        if (!full.owner) {
          const icon = document
            .querySelector(".js-locked-wrapper")
            .cloneNode(true).outerHTML;
          return `<div class="icon-wrapper">
              ${icon}${checkbox}
            </div>`;
        }
        return checkbox;
      },
      targets: 0
    },
    {
      targets: [COLUMNS.SAMPLE_NAME],
      render(data, type, full) {
        const link = createItemLink({
          url: `${window.TL.BASE_URL}projects/${full.projectId}/samples/${
            full.id
          }`,
          label: full.sampleName,
          classes: ["t-sample-label"]
        });

        /*
        Display a notification if there are any issues with QC.
         */
        if (full.qcEntries.length) {
          const icon = document
            .querySelector(".js-qc-warning-wrapper")
            .cloneNode(true);
          /*
          Generate the content for the popover
           */
          const content = `<ul class="popover-list">
              ${full.qcEntries
                .map(qc => `<li class="error">${qc}</li>`)
                .join("")}
          </ul>`;
          icon.setAttribute("data-content", content);
          return `<div class="icon-wrapper">${icon.outerHTML}${link}</div>`;
        }
        return link;
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
    /*
    Ensure the data stored can be used to save the sample the cart.
     */
    row.dataset.info = JSON.stringify({
      projectId: data.projectId,
      id: data.id,
      sampleName: data.sampleName
    });
    /*
    If there are QC errors, highlight the row
     */
    if (data.qcEntries.length) {
      $row.addClass("row-warning");
    }
  }
});

const $dt = $table.DataTable(config);

function checkToolButtonState(count = $dt.select.selected()[0].size) {
  /*
  Update the state of the buttons in the navbar.
   */
  for (const btn of SAMPLE_TOOL_BUTTONS) {
    btn.checkState(count, ASSOCIATED_PROJECTS.size > 0, IS_REMOTE_PROJECT);
  }
}

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
      ASSOCIATED_PROJECTS.size === ASSOCIATED_INPUTS.length
    );
    // Update the DataTable
    $dt.ajax.reload(null, false);
  }, 0);

  checkToolButtonState();
  $(event.target).blur();
  return false;
});

/*
TABLE EVENT HANDLERS
 */

// Row selection events.
$dt.on("selection-count.dt", function(e, count) {
  checkToolButtonState(count);
});

/*
Handle opening the Sample Tools modals.
 */
$("#js-modal-wrapper").on("show.bs.modal", function(event) {
  const wrapper = this;
  const modal = $(wrapper);
  /*
  Determine which modal to open
   */
  const btn = event.relatedTarget;
  const url = btn.data("url");
  const params = btn.data("params") || {};
  const script_src = btn.data("script");
  /*
  Find the ids for the currently selected samples.
   */
  params["sampleIds"] = getSelectedIds();

  let script;
  modal.load(`${url}?${$.param(params)}`, function() {
    if (typeof script_src !== "undefined") {
      script = document.createElement("script");
      script.type = "text/javascript";
      script.src = script_src;
      document.getElementsByTagName("head")[0].appendChild(script);
    }
  });

  /*
  Handle the closing the modal
   */
  modal.on(SAMPLE_EVENTS.SAMPLE_TOOLS_CLOSED, function(e) {
    modal.modal("hide");
    $dt.select.selectNone();
    $dt.ajax.reload();
    // Remove the script
    if (typeof script !== "undefined") {
      document.getElementsByTagName("head")[0].removeChild(script);
      script = undefined;
    }
  });

  /*
  Clear the content of the modal when it is closed.
   */
  modal.on("hidden.bs.modal", function() {
    modal.empty();
  });
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
    TABLE_FILTERS.set(FILTERS.FILTER_BY_FILE, [...new Set(contents)]);
    // Refresh the table.
    $dt.ajax.reload(() => {
      $dt.select.selectAll();
    });
  };
  reader.readAsText(file);
}

const filterByFileInput = document.querySelector("#filter-by-file");
filterByFileInput.addEventListener("change", handleFileSelect, false);

/*
Set up specific filters modal
 */
$("#js-filter-modal-wrapper").on("show.bs.modal", function() {
  const $wrapper = $(this);
  const template = $wrapper.data("template");
  const scriptUrl = $wrapper.data("script");
  const params = {};

  if (ASSOCIATED_PROJECTS.size > 0) {
    // Add a list of ids for currently visible associated projects
    params.associated = Array.from(ASSOCIATED_PROJECTS.keys());
  }

  if (TABLE_FILTERS.has(FILTERS.FILTER_BY_NAME)) {
    params.name = TABLE_FILTERS.get(FILTERS.FILTER_BY_NAME);
  }

  if (TABLE_FILTERS.has(FILTERS.FILTER_BY_ORGANISM)) {
    params.organism = TABLE_FILTERS.get(FILTERS.FILTER_BY_ORGANISM);
  }

  if (
    TABLE_FILTERS.has(FILTERS.FILTER_BY_EARLY_DATE) &&
    TABLE_FILTERS.has(FILTERS.FILTER_BY_LATEST_DATE)
  ) {
    params.startDate = TABLE_FILTERS.get(FILTERS.FILTER_BY_EARLY_DATE);
    params.endDate = TABLE_FILTERS.get(FILTERS.FILTER_BY_LATEST_DATE);
  }

  let script;
  $wrapper.load(`${template}?${$.param(params)}`, function() {
    script = document.createElement("script");
    script.type = "text/javascript";
    script.src = scriptUrl;
    document.getElementsByTagName("head")[0].appendChild(script);
  });

  /*
  Handle applying the filters
   */
  $wrapper.on(SAMPLE_EVENTS.SAMPLE_FILTER_CLOSED, function(e, filters) {
    /*
    Add the filters to the table parameters
     */
    for (const filter in filters) {
      if (filters.hasOwnProperty(filter)) {
        TABLE_FILTERS.set(filter, filters[filter]);
      }
    }

    /*
    Reload the table to apply the filters
     */
    $dt.ajax.reload();
  });
});

/*
Set up the ability to clear all filters
 */
function clearFilters() {
  /*
  Clear file filter
   */
  document.querySelector("#filter-by-file").value = null;
  /*
  Clear custom table filters
   */
  TABLE_FILTERS.clear();
  /*
  Clear DataTables default search
   */
  $dt.search("");
  /*
  De-select all items in the table
   */
  $dt.select.selectNone();
  /*
  Reload the table.
   */
  $dt.ajax.reload();
}

const clearFilterBtn = document.querySelector(".js-clear-filters");
clearFilterBtn.addEventListener("click", clearFilters, false);

/**
 * Display any filters that are applied to the table and give the user a quick way to remove them.
 * @param  {Map} filters currently applied to the table.
 */
function displayFilters(filters) {
  // This should be set by datatable.
  const table = this;
  const $wrapper = $(`<div class="filter-chip--wrapper"></div>`);

  function createChip(name, value, handler) {
    const $chip = $(
      `<span class="filter-chip--chip">${name} : ${value} <i class="fa fa-times-circle filter-chip--close" title="remove" aria-hidden="true"></i></span>`
    );
    $chip.on("click", ".filter-chip--close", handler);
    $wrapper.append($chip);
  }

  if (filters.has(FILTERS.FILTER_BY_NAME)) {
    createChip(
      window.PAGE.i18n.chips.name,
      filters.get(FILTERS.FILTER_BY_NAME),
      () => {
        filters.delete(FILTERS.FILTER_BY_NAME);
        table.ajax.reload();
      }
    );
  }

  if (filters.has(FILTERS.FILTER_BY_ORGANISM)) {
    createChip(
      window.PAGE.i18n.chips.organism,
      filters.get(FILTERS.FILTER_BY_ORGANISM),
      () => {
        filters.delete(FILTERS.FILTER_BY_ORGANISM);
        table.ajax.reload();
      }
    );
  }

  if (
    filters.has(FILTERS.FILTER_BY_EARLY_DATE) &&
    filters.has(FILTERS.FILTER_BY_LATEST_DATE)
  ) {
    const start = moment(filters.get(FILTERS.FILTER_BY_EARLY_DATE)).format(
      "ll"
    );
    const end = moment(filters.get(FILTERS.FILTER_BY_LATEST_DATE)).format("ll");
    const range = `${start} - ${end}`;
    createChip(window.PAGE.i18n.chips.range, range, () => {
      filters.delete(FILTERS.FILTER_BY_EARLY_DATE);
      filters.delete(FILTERS.FILTER_BY_LATEST_DATE);
      table.ajax.reload();
    });
  }

  $(".filter-tags").html($wrapper);
}
/*
Activate page tooltips
 */
$('[data-toggle="popover"]').popover();
