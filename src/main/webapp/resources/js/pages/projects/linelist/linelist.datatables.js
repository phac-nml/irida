/* eslint new-cap: [2, {'capIsNewExceptions': ['DataTable']}] */
import {EVENTS} from './constants';
import {formatBasicHeaders} from '../../../utilities/datatables.utilities';
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('datatables.net-scroller');
require('datatables.net-colreorder');
require('style!datatables.net-scroller-bs/css/scroller.bootstrap.css');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
require('style!datatables.net-colreorder-bs/css/colReorder.bootstrap.css');

const headers = formatBasicHeaders(Object.assign(window.headersList));

const $table = $(`#linelist`).DataTable({
  data: Object.assign(window.metadataList),
  columns: headers,
  scrollX: true,
  scrollY: 600,
  deferRender: true,
  scroller: true,
  colReorder: true
});

$table.on('column-reorder', (e, settings, detail) => {
  const event = new CustomEvent(EVENTS.TABLE.colReorder, {detail,
    bubbles: true});
  e.currentTarget.dispatchEvent(event);
});

document.body.addEventListener(EVENTS.TABLE.columnVisibility, e => {
  const columnName = e.detail.column;
  if (columnName) {
    const column = $table.column(columnName);
    column.visible(!column.visible());
  }
});
