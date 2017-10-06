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
import { CART } from "../../../utilities/events-utilities";
import { GetOrderedColour } from "../../../utilities/colour.utilities";

/**
 * Reference to the currently selected associated projects.
 * @type {Map}
 */
const ASSOCIATED_PROJECTS = new Map();

/**
 * Reference to the colour for a specific project.
 * @type {Map}
 */
const PROJECT_COLOURS = new Map();

/**
 * Generator for the project colours
 * @type {GetOrderedColour}
 */
const COLOUR_PICKER = new GetOrderedColour();

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
    }
  },
  stateSave: true,
  deferRender: true,
  select: {
    allUrl: window.PAGE.urls.samples.sampleIds,
    allPostDataFn() {
      console.log([...ASSOCIATED_PROJECTS.keys()]);
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
        /*
        Each project gets its own colour bar next to the name of the project.
         */
        let colour;
        if (PROJECT_COLOURS.has(data)) {
          colour = PROJECT_COLOURS.get(data);
        } else {
          colour = COLOUR_PICKER.getNext();
          PROJECT_COLOURS.set(data, colour);
        }
        return createItemLink({
          url: `${window.TL.BASE_URL}projects/${full.projectId}`,
          label: `<div class="label-bar-color" style="background-color: ${colour}">&nbsp;</div>${data}`,
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

  /*
  Update the cart with the new samples.
   */
  const event = new CustomEvent(CART.ADD, { detail: { projects } });
  document.dispatchEvent(event);
});

/*
ASSOCIATED PROJECTS
 */

// This allows for the use of checkboxes in the dropdown without
// it closing on every click.
const ASSOCIATED_INPUTS = $(".associated-cb input");
$(".associated-dd .dropdown-menu a").on("click", function(event) {
  const li = $(this).parent();
  const $target = $(event.currentTarget);
  const $inp = $target.find("input");
  const id = $inp.val();
  /*
  This seems backwards, but the checkbox has not yet updated itself at this point.
   */
  const checked = !$inp.prop("checked");

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

    // Update this input
    setTimeout(function() {
      $inp.prop("checked", checked);
      // Update the DataTable
      $dt.ajax.reload(null, false);
    }, 0);
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
  Update the state of the cart button.
  If there is nothing selected, disable the button.
   */
  $cartBtn.prop("disabled", count === 0);
});
