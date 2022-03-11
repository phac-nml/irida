import axios from "axios";
import "bootstrap-daterangepicker";
import "bootstrap-daterangepicker/daterangepicker.css";
import chroma from "chroma-js";
import $ from "jquery";
import moment from "moment";
import "../../../../css/pages/project-samples.css";
import { putSampleInCart } from "../../../apis/cart/cart";
import {
  createItemLink,
  generateColumnOrderInfo,
  tableConfig,
} from "../../../utilities/datatables-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { download, downloadPost } from "../../../utilities/file-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import "./../../../vendor/datatables/datatables";
import "./../../../vendor/datatables/datatables-buttons";
import "./../../../vendor/datatables/datatables-rowSelection";
import "./add-sample/AddSampleButton";
import { FILTERS, SAMPLE_EVENTS } from "./constants";

import "./linker/Linker";
import {
  SampleCartButton,
  SampleDropdownButton,
  SampleExportButton,
  SampleProjectDropdownButton,
} from "./SampleButtons";
import "./ShareSamplesLink";

/*
When the page loads clear any previously stored samples from the session storage
*/
window.onload = () => window.sessionStorage.removeItem("share");

/*
This is required to use select2 inside a modal.
This is required to use select2 inside a modal.
 */
$.fn.modal.Constructor.prototype.enforceFocus = function () {};

/*
Keep a reference to all the modals that have been loaded onto the page
 */
window.IRIDA = window.IRIDA = {
  modals: {},
};

/*
Defaults for table popovers
 */
const POPOVER_OPTIONS = {
  container: "body",
  trigger: "hover",
  placement: "right",
  sanitize: false,
  html: true,
  template: $("#popover-template").clone(),
};

const IS_REMOTE_PROJECT = window.PAGE.isRemoteProject;

/*
 Initialize the sample tools menu.  This is used to check the status of the buttons.
 */
const sampleToolsNodes = document.querySelectorAll(".js-sample-tool-btn");
const SAMPLE_TOOL_BUTTONS = [...sampleToolsNodes].map(
  (elm) => new SampleDropdownButton(elm)
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
Hack to get the sample ids from the Linker react component.
 */
document.addEventListener(
  "sample-ids",
  function (e) {
    const event = new CustomEvent("sample-ids-return", {
      detail: {
        sampleIds: getSelectedIds(),
        projectId: window.project.id,
      },
    });
    document.dispatchEvent(event);
  },
  false
);

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

    /*
    Get the default datatable params
     */
    const {
      sampleNames,
      associated,
      search,
      name,
      organism,
      startDate,
      endDate,
    } = $dt.ajax.params();

    /*
    These are default params must be included.
     */
    const data = {
      sampleNames: sampleNames || [],
      search: search.value,
      name: name || "",
      type: this.data("file"),
    };

    /*
    Only add the following if they are needed,
     */
    if (associated) {
      data.associated = associated;
    }

    if (organism) {
      data.organism = organism;
    }

    if (startDate && endDate) {
      data.startDate = startDate;
      data.endDate = endDate;
    }

    downloadPost(`${url}`, data);
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
  },
};
[...document.querySelectorAll(".js-sample-export-btn")].forEach((elm) => {
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
[...document.querySelectorAll(".js-sample-project-tool-btn")].forEach((elm) => {
  SAMPLE_TOOL_BUTTONS.push(
    new SampleProjectDropdownButton(elm, IS_REMOTE_PROJECT)
  );
});

/*
Initialize the add to cart button
 */
const cartBtn = new SampleCartButton($(".js-cart-btn"), function () {
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
  Object.keys(projects).forEach((id) => {
    putSampleInCart(+id, projects[id]);
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
const PROJECT_COLOURS = (function () {
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

      displayFilters(TABLE_FILTERS);
    },
  },
  stateSave: true,
  deferRender: true,
  select: {
    allUrl: window.PAGE.urls.samples.sampleIds,
    allPostDataFn() {
      return {
        associated: [...ASSOCIATED_PROJECTS.keys()],
      };
    },
    formatSelectAllResponseFn(response) {
      // This is a callback function used by datatables-select
      // to format the server response when selectAll is clicked.
      // It puts the response into the format of the `data-info` attribute
      // set on the row itself ({row_id: {projectId, sampleId}}
      const complete = new Map();
      response.forEach((sample) => complete.set(`row_${sample.id}`, sample));
      return complete;
    },
  },
  order: [[COLUMNS.MODIFIED_DATE, "desc"]],
  rowId: "DT_RowId",
  buttons: ["selectAll", "selectNone"],
  language: {
    select: {
      none: i18n("project.samples.counts.none"),
      one: i18n("project.samples.counts.one"),
      other: i18n("project.samples.counts.more"),
    },
    buttons: {
      selectAll: i18n("project.samples.select.selectAll"),
      selectNone: i18n("project.samples.select.selectNone"),
    },
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
      targets: 0,
    },
    {
      targets: [COLUMNS.SAMPLE_NAME],
      render(data, type, full) {
        const link = createItemLink({
          url: setBaseUrl(`/projects/${full.projectId}/samples/${full.id}`),
          label: full.sampleName,
          classes: ["t-sample-label"],
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
                .map((qc) => `<li class="error">${qc}</li>`)
                .join("")}
          </ul>`;
          icon.setAttribute("data-content", content);
          return `<div class="icon-wrapper">${icon.outerHTML}${link}</div>`;
        }
        return link;
      },
    },
    {
      targets: [COLUMNS.PROJECT_NAME],
      render(data, type, full) {
        return createItemLink({
          url: setBaseUrl(`projects/${full.projectId}`),
          label: `<div class="label-bar-color" style="background-color: ${PROJECT_COLOURS.get(
            full.projectId
          )}">&nbsp;</div>${data}`,
          classes: ["project-link"],
        });
      },
    },
    {
      targets: [COLUMNS.CREATED_DATE, COLUMNS.MODIFIED_DATE],
      render(data) {
        return `<time>${formatInternationalizedDateTime(data)}</time>`;
      },
    },
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
      projectName: data.projectName,
      id: data.id,
      sampleName: data.sampleName,
      owner: data.owner,
    });
    /*
    If there are QC errors, highlight the row
     */
    if (data.qcEntries.length) {
      $row.addClass("row-warning");
    }
  },
});

const $dt = (window.$dt = $table.DataTable(config));

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
$(".associated-dd .dropdown-menu a").on("click", function (event) {
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

  setTimeout(function () {
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
$dt.on("selection-count.dt", function (e, count) {
  checkToolButtonState(count);
});

/*
Handle opening the Sample Tools modals.
 */
$("#js-modal-wrapper").on("show.bs.modal", function (event) {
  const wrapper = this;
  const modal = $(wrapper);
  /*
  Determine which modal to open
   */
  const btn = event.relatedTarget;
  const scriptId = btn.data("script-id");
  const url = btn.data("url");
  const params = btn.data("params") || {};
  const script_src = btn.data("script");
  /*
  Find the ids for the currently selected samples.
   */
  const sampleIds = getSelectedIds();

  let script;
  modal.load(`${url}`, { sampleIds, ...params }, function () {
    if (typeof window.IRIDA.modals[scriptId] === "function") {
      window.IRIDA.modals[scriptId]();
    } else if (typeof script_src !== "undefined") {
      script = document.createElement("script");
      script.type = "text/javascript";
      script.src = script_src;
      document.getElementsByTagName("head")[0].appendChild(script);
    }
  });

  /*
  Handle the closing the modal
   */
  modal.on(SAMPLE_EVENTS.SAMPLE_TOOLS_CLOSED, function (e) {
    modal.modal("hide");
    $dt.select.selectNone();
    $dt.ajax.reload();
  });

  /*
  Clear the content of the modal when it is closed.
   */
  modal.on("hidden.bs.modal", function () {
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
  reader.onload = function (e) {
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
$("#js-filter-modal-wrapper").on("show.bs.modal", function () {
  const modal = $(this);

  /*
  Initial all the filter form inputs
   */
  const $name = modal.find("#js-name");
  $name.val(TABLE_FILTERS.get(FILTERS.FILTER_BY_NAME));

  const $organism = modal.find("#js-organism");
  $organism.val(TABLE_FILTERS.get(FILTERS.FILTER_BY_ORGANISM));

  // Get a list of organisms based on associated projects.
  const data = {
    associated: ASSOCIATED_PROJECTS.size
      ? Array.from(ASSOCIATED_PROJECTS.keys())
      : [],
  };
  axios
    .get(
      setBaseUrl(setBaseUrl(`/projects/${window.project.id}/filter/organisms`))
    )
    .then(({ data }) => {
      $organism.empty();
      $organism.append(`<option value="">---</option>`);
      data.stringList.forEach((organism) =>
        $organism.append(`<option value="${organism}">${organism}</option>`)
      );
    });

  function formatDateRangeInput(start, end) {
    $dateRangeFilter.val(
      `${start.format(
        i18n("project.sample.filter.date.format")
      )} - ${end.format(i18n("project.sample.filter.date.format"))}`
    );
  }

  /*
  Set up the date range filter.
  This is based of off jquery date range picker (http://www.daterangepicker.com/)
   */
  const $dateRangeFilter = $("#js-daterange")
    .daterangepicker({
      autoUpdateInput: false,
      locale: {
        cancelLabel: "Clear",
      },
      showDropdowns: true,
      ranges: {
        [i18n("project.sample.filter.date.month")]: [
          moment().subtract(1, "month"),
          moment(),
        ],
        [i18n("project.sample.filter.date.months3")]: [
          moment().subtract(3, "month"),
          moment(),
        ],
        [i18n("project.sample.filter.date.months6")]: [
          moment().subtract(6, "month"),
          moment(),
        ],
        [i18n("project.sample.filter.date.year")]: [
          moment().subtract(1, "year"),
          moment(),
        ],
      },
    })
    .on("apply.daterangepicker", function (ev, picker) {
      /*
      Call the the apply button is clicked.
      Formats the dates into human readable form.  This is required since we disabled
      the update of the input field (autoUpdateInput: false) to allow for an empty field to begin with.
       */
      formatDateRangeInput(picker.startDate, picker.endDate);
    })
    .on("cancel.daterangepicker", function () {
      $(this).val("");
    });

  /*
  Handle applying the filters
   */
  modal.on("hide.bs.modal", function (e, filters) {
    if ($name.val()) {
      TABLE_FILTERS.set(FILTERS.FILTER_BY_NAME, $name.val());
    } else {
      TABLE_FILTERS.delete(FILTERS.FILTER_BY_NAME);
    }

    if ($organism.val()) {
      TABLE_FILTERS.set(FILTERS.FILTER_BY_ORGANISM, $organism.val());
    } else {
      TABLE_FILTERS.delete(FILTERS.FILTER_BY_ORGANISM);
    }

    // Check to see if the date range filter needs to be applied.
    if ($dateRangeFilter.val()) {
      const dateranges = $dateRangeFilter.data("daterangepicker");
      const startDate = dateranges.startDate.toDate().getTime();
      const endDate = dateranges.endDate.toDate().getTime();

      if (!isNaN(startDate) && !isNaN(endDate)) {
        TABLE_FILTERS.set(FILTERS.FILTER_BY_EARLY_DATE, startDate);
        TABLE_FILTERS.set(FILTERS.FILTER_BY_LATEST_DATE, endDate);
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
 */
function displayFilters(filters) {
  // This should be set by datatable.
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
      i18n("project.sample.filter-name"),
      filters.get(FILTERS.FILTER_BY_NAME),
      () => {
        filters.delete(FILTERS.FILTER_BY_NAME);
        $dt.ajax.reload();
      }
    );
  }

  if (filters.has(FILTERS.FILTER_BY_ORGANISM)) {
    createChip(
      i18n("project.sample.filter-organism"),
      filters.get(FILTERS.FILTER_BY_ORGANISM),
      () => {
        filters.delete(FILTERS.FILTER_BY_ORGANISM);
        $dt.ajax.reload();
      }
    );
  }

  if (
    filters.has(FILTERS.FILTER_BY_EARLY_DATE) &&
    filters.has(FILTERS.FILTER_BY_LATEST_DATE)
  ) {
    const dateFormat = { year: "numeric", month: "long", day: "numeric" };
    const start = formatInternationalizedDateTime(
      filters.get(FILTERS.FILTER_BY_EARLY_DATE),
      dateFormat
    );
    const end = formatInternationalizedDateTime(
      filters.get(FILTERS.FILTER_BY_LATEST_DATE),
      dateFormat
    );
    const range = `${start} - ${end}`;
    createChip(i18n("project.sample.filter-date.label"), range, () => {
      filters.delete(FILTERS.FILTER_BY_EARLY_DATE);
      filters.delete(FILTERS.FILTER_BY_LATEST_DATE);
      $dt.ajax.reload();
    });
  }

  $(".filter-tags").html($wrapper);
}
/*
Activate page tooltips
 */
$('[data-toggle="popover"]').popover();
