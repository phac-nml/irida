/* eslint new-cap: [2, {"capIsNewExceptions": ["DataTable"]}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('datatables.net-buttons');
require('datatables.net-buttons-bs');
require('datatables.net-buttons/js/buttons.colVis.js');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');

$('#linelist').DataTable({
  dom: 'Bfrtip',
  buttons: [
    'colvis'
  ],
  scrollX: true
});
