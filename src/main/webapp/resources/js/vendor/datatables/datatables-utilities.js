const $ = require('jquery');
const _ = require('lodash');
const moment = require('moment');

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
export const dom = `
<".row"
  <"col-md-6 col-sm-12 buttons"B><"#dt-filters.col-md-6 col-sm-12"f>>
<"dt-table-wrapper"rt>
<"row"
  <"col-md-3 col-sm-12"l>
  <" col-md-6 col-sm-12"p><"col-md-3 col-sm-12 text-right"i>>`;

/**
 * Create a DOM element that contains a "time since" string.  If it is not a valid
 * date that is passed, it will return the original value.
 * @param {string} data date to add to the string
 * @return {*} formatted date DOM or the initial value
 */
export function formatDateFromNowDOM({data}) {
  if (moment.isDate(new Date(data))) {
    const date = moment(new Date(data));
    return `
<time data-toggle="tooltip" data-placement="top" 
      title="${date.format("LLL")}">${date.fromNow()}</time>
  `;
  }
  return data;
}

/**
 * Create a DOM element with an actual date.
 * @param {string} data date to format
 * @return {*} formatted date DOM of the initial value.
 */
export function formatDateDOM({data}) {
  if (moment.isDate(new Date(data))) {
    const date = moment(new Date(data));
    return `
<time>${date.format("LLL")}</time>    
`;
  }
  return data;
}

/**
 * Create a button to download a file.
 * @param {string} url of the item to download
 * @param {title} title to label the download by.
 * @return {object} DOM anchor element for download.
 */
export function createDownloadLink({url, title}) {
  const anchor = document.createElement('a');
  anchor.classList.add('btn', 'btn-default', 'download-btn');
  anchor.download = title;
  anchor.setAttribute('href', url);
  anchor.innerHTML = `<i class="fa fa-download fa-fw"
               data-toggle="tooltip" data-placement="top"
               title="Download"></i>`;
  return anchor;
}

/**
 * Create a button to delete a row.
 * @param {object} data attributes need to delete an item.
 * @return {object} DOM element for button.
 */
export function createDeleteBtn(data = {}) {
  const btn = document.createElement('button');
  btn.classList.add('btn', 'btn-default', 'remove-btn');
  // Add any required data attributes.
  Object.assign(btn.dataset, data);
  btn.innerHTML = `<i class="fa fa-trash fa-fw" 
            data-toggle="tooltip" data-placement="top"
            title="Delete"></i>`;
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
    const btns = wrapper.find('.btn-group');
    for (const btn of buttons) {
      btns.append(btn);
    }
    return wrapper.html();
  }
  return '';
}

/**
 * Create an anchor tag to link to an item.
 * @param {string} url to find the item at
 * @param {string} label label for the item
 * @return {*} anchor element containing link.
 */
export function createItemLink({url, label} = {}) {
  if (typeof url !== 'undefined' && typeof label !== 'undefined') {
    return `
  <a class="btn btn-link wrap-cell" href="${url}">${label}</a>
  `;
  }
  return label || '';
}

/**
 * Generate the a human readable form from a date.
 * @param {string} date to format
 * @return {string} humanized version of the date
 */
export function getHumanizedDate({date}) {
  return moment.duration(date).humanize();
}

/**
 * Get the order of the columns on the page.
 * @return {object} {{COLUMN_NAME: index}}
 */
export function generateColumnOrderInfo() {
  const columns = {};
  $('thead th').each((index, elm) => {
    const data = _.snakeCase($(elm).data('data')).toUpperCase();
    columns[data] = index;
  });
  return columns;
}

/**
 * Activate any tooltips on a table row.
 * @param {object} row DOM for a TR
 */
export function activateTooltips(row) {
  $(row).tooltip({selector: '[data-toggle="tooltip"]'});
}
