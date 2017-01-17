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

$(`#linelist`).DataTable({
  data: window.metadataList,
  dom: domButtonsScroller,
  buttons: [
    'colvis'
  ],
  scrollX: true,
  scrollY: 600,
  deferRender: true,
  scroller: true
});
