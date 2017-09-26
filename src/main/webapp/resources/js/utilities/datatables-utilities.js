import $ from "jquery";
import _ from "lodash";
import { addTooltip } from "./bootstrap-utilities";
import { createIcon, ICONS } from "./fontawesome-utilities";

/*
<div class="row">
  <div class="col-md-6 col-sm-12 buttons">
    [BUTTONS]
  </div>
  <div class="col-md-6 col-sm-12 dt-filters">
    [FILTER}
  </div>
</div>
[PROCESSING]
[TABLE]
<div class="row">
  <div class="col-md-3 col-sm-12">
    [LENGTH]
  </div>
  <div class="col-md-6 col-sm-12">
    [PAGING]
  </div>
  <div class="col-md-3 col-sm-12">
    [INFO]
  </div>
</div>
*/
const dom = `
<".row"
  <"col-md-6 col-sm-12 buttons"B><"#dt-filters.col-md-6 col-sm-12"f>>
  <".row"
    <"col-md-12 filter-tags"<"filter-tags__space">>
  >
<"dt-table-wrapper"rt>
<"row"
  <"col-md-3 col-sm-12"l>
  <" col-md-6 col-sm-12"p><"col-md-3 col-sm-12 text-right"i>>`;

/**
 * Default DataTables configuration object.  Anything can be overwritten,
 * but this will provide a baseline for all DataTables.
 */
export const tableConfig = {
  dom,
  processing: true,
  serverSide: true,
  createdRow(row) {
    $(row).tooltip({ selector: "[data-toggle='tooltip']" });
  }
};

/**
 * Create a button to download a file.
 * @param {string} url of the item to download
 * @param {title} title to label the download by.
 * @return {object} DOM anchor element for download.
 */
export function createDownloadLink({ url, title }) {
  const anchor = document.createElement("a");
  anchor.classList.add("btn", "btn-default", "download-btn");
  anchor.download = title;
  anchor.setAttribute("href", url);
  addTooltip({ dom: anchor, title: "Download" });

  const icon = createIcon({ icon: ICONS.download, fixed: true });
  anchor.append(icon);
  return anchor;
}

/**
 * Create a button to delete a row.
 * @param {object} data attributes need to delete an item.
 * @return {object} DOM element for button.
 */
export function createDeleteBtn(data = { title: "Delete" }) {
  const btn = document.createElement("button");
  btn.classList.add("btn", "btn-default", "remove-btn");
  addTooltip({ dom: btn, title: data.title });
  const icon = createIcon({ icon: ICONS.trash, fixed: true });
  btn.appendChild(icon);
  return btn;
}

/**
 * Create a button group containing action buttons for this row.
 * @param {array} buttons list of DOM nodes for buttons.
 * @return {*} Either the button group or an empty string.
 */
export function createButtonCell(buttons = []) {
  if (buttons.length) {
    const wrapper = $(`<div>
<div class="btn-group btn-group-xs pull-right" style="display: flex;"></div>
</div>`);
    const btns = wrapper.find(".btn-group");
    for (const btn of buttons) {
      btns.append(btn);
    }
    return wrapper.html();
  }
  return "";
}

/**
 * Create an anchor tag to link to an item.
 * @param {string} url to find the item at
 * @param {string} label label for the item
 * @param {string} width width for the button
 * @param {array} list of extra classes to add to the link
 * @return {*} anchor element containing link.
 */
export function createItemLink({ url, label, width = "160px", classes = [] }) {
  if (typeof url !== "undefined" && typeof label !== "undefined") {
    const link = document.createElement("a");
    link.classList.add("btn", "btn-link", "dt-wrap-cell", ...classes);
    link.style.width = width;
    link.style.textAlign = "left";
    link.href = url;
    link.innerHTML = label;

    return link.outerHTML;
  }
  return label || "";
}

/**
 * Get the order of the columns on the page.
 * @return {object} {{COLUMN_NAME: index}}
 */
export function generateColumnOrderInfo() {
  const columns = {};
  $("thead th").each((index, elm) => {
    const data = _.snakeCase($(elm).data("data")).toUpperCase();
    columns[data] = index;
  });
  return columns;
}

/**
 * Make the content of a column a fixed width.  This will add an ellipsis to the text
 * if too long.
 * @param {string} text content
 * @param  {number} width for the column
 * @return {Element} formatted DOM as text
 */
export function createRestrictedWidthContent({ text, width = 150 }) {
  const dom = document.createElement("div");
  dom.classList.add("cell-restricted");
  dom.style.width = `${width}px`;
  dom.innerText = text;
  return addTooltip({ dom, title: text });
}

/**
 * Wrap the contents of a cell in a div that will force wrapping at the desired width.
 * @param {string} text to put in cell
 * @param {string} width desired (e.g. '100px' or '50em')
 * @return {string} formatted DOM as text
 */
export function wrapCellContents({ text, width = "250px" }) {
  const div = document.createElement("div");
  div.classList.add("dt-wrap-cell");
  div.style.width = width;
  div.innerHTML = text;
  return div.outerHTML;
}

/**
 * Create a filter tag on the datatable
 * @param {string} text value searched
 * @param {string} type field searched
 * @param {function} handler what to do when button is clicked
 */
export function createFilterTag({ text, type, handler }) {
  const remove = elm => {
    $(elm)
      .off("click")
      .remove();
  };
  const $filterTags = $(".filter-tags");

  // Check to see if that button already exists.
  const btn = $filterTags.find(`[data-type="${type}"]`);
  if (btn) {
    remove(btn);
  }

  if (text) {
    const tag = `
  <button data-type="${type}" class="btn btn-default btn-xs filter-tags__tag">
      <b>${type}</b> : ${text} ${createIcon({ icon: ICONS.remove, fixed: true })
      .outerHTML}
  </button>`;
    const $tag = $(tag);
    $tag.on("click", function() {
      // Remove tag
      remove(this);
      handler();
    });
    $filterTags.append($tag);
  }
}
