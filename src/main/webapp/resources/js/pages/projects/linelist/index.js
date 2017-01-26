/* eslint new-cap: [2, {'capIsNewExceptions': ['DataTable']}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('datatables.net-buttons');
require('datatables.net-buttons-bs');
require('datatables.net-buttons/js/buttons.colVis.js');
require('datatables.net-scroller');
require('style!datatables.net-scroller-bs/css/scroller.bootstrap.css');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
import {domButtonsScroller} from '../../../utilities/datatables.utilities';

const data = window.metadataList.map(row => {
  return row.map(cell => cell.value || '');
});

$(`#linelist`).DataTable({
  data,
  dom: domButtonsScroller,
  buttons: [
    'colvis'
  ],
  scrollX: true,
  scrollY: 600,
  deferRender: true,
  scroller: true
});
