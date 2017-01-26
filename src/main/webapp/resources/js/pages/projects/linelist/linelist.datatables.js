/* eslint new-cap: [2, {'capIsNewExceptions': ['DataTable']}] */
import {EVENTS} from './constants';
import {dom} from '../../../utilities/datatables.utilities';
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('datatables.net-scroller');
require('datatables.net-colreorder');
require('style!datatables.net-scroller-bs/css/scroller.bootstrap.css');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
require('style!datatables.net-colreorder-bs/css/colReorder.bootstrap.css');

// window.metadataList is not in a format that Datatables
// can use.  This transforms it into an array of values.
const data = window.metadataList.map(row => {
  return window.headersList.map(header => row[header].value);
});

// Initialize the Datatable on the page.
const table = $(`#linelist`).DataTable({
  data,
  dom,
  scrollX: true,
  scrollY: '50vh',
  scrollCollapse: true,
  scroller: true,
  colReorder: true
});

// Move the toolbar inside the table.
const toolbar = document.querySelector('.toolbar');
toolbar.appendChild(document.getElementsByTagName('metadata-component')[0]);

table.on('column-reorder', (e, settings, detail) => {
  const event = new CustomEvent(EVENTS.TABLE.colReorder, {detail,
    bubbles: true});
  e.currentTarget.dispatchEvent(event);
});

document.body.addEventListener(EVENTS.TABLE.columnVisibility, e => {
  const columnName = e.detail.column;
  if (columnName) {
    const column = table.column(columnName);
    column.visible(!column.visible());
  }
});
