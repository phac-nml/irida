import $ from 'jquery';
import _ from 'lodash';
import {addTooltip} from './bootstrap-utilities';
import {createIcon, ICONS} from './fontawesome-utilities';

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
    <".col-md-12">
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
    $(row).tooltip({selector: "[data-toggle='tooltip']"});
  }
};

/**
 * Create a button to download a file.
 * @param {string} url of the item to download
 * @param {title} title to label the download by.
 * @return {object} DOM anchor element for download.
 */
export function createDownloadLink({url, title}) {
  const anchor = document.createElement("a");
  anchor.classList.add("btn", "btn-default", "download-btn");
  anchor.download = title;
  anchor.setAttribute("href", url);

  const icon = createIcon({icon: ICONS.download, fixed: true});
  const tooltiped = addTooltip({dom: icon, title: "Download"});
  anchor.append(tooltiped);
  return anchor;
}

/**
 * Create a button to delete a row.
 * @param {object} data attributes need to delete an item.
 * @return {object} DOM element for button.
 */
export function createDeleteBtn(data = {}) {
  const btn = document.createElement("button");
  btn.classList.add("btn", "btn-default", "remove-btn");
  // Add any required data attributes.
  Object.assign(btn.dataset, data);

  const icon = createIcon({icon: ICONS.trash, fixed: true});
  const tooltiped = addTooltip({dom: icon, title: "Delete"});
  btn.append(tooltiped);
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
 * @return {*} anchor element containing link.
 */
export function createItemLink({url, label} = {}) {
  if (typeof url !== "undefined" && typeof label !== "undefined") {
    return `
  <a class="btn btn-link wrap-cell" href="${url}">${label}</a>
  `;
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
export function createRestrictedWidthContent({text, width = 150}) {
  const dom = document.createElement("div");
  dom.classList.add("cell-restricted");
  dom.style.width = `${width}px`;
  dom.innerText = text;
  return addTooltip({dom, title: text});
}
