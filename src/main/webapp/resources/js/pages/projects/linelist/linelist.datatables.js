/* eslint new-cap: [2, {'capIsNewExceptions': ['DataTable']}] */
import {EVENTS} from './constants';
import {formatBasicHeaders} from '../../../utilities/datatables.utilities';
const $ = require('jquery');
require('datatables.net');

require('datatables-bootstrap3-plugin');
require('datatables.net-scroller');
require('style!datatables.net-scroller-bs/css/scroller.bootstrap.css');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');

const headers = formatBasicHeaders(window.headersList);

const $table = $(`#linelist`).DataTable({
  data: window.metadataList,
  columns: headers,
  scrollX: true,
  scrollY: 600,
  deferRender: true,
  scroller: true
});

document.body.addEventListener(EVENTS.TABLE.columnVisibility, e => {
  const columnName = e.detail.column;
  if (columnName) {
    const column = $table.column(columnName);
    column.visible(!column.visible());
  }
});
