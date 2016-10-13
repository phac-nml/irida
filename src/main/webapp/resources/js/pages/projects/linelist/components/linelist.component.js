/* eslint new-cap: [2, {"capIsNewExceptions": ["DataTable"]}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('datatables.net-buttons');
require('datatables.net-buttons-bs');
require('datatables.net-buttons/js/buttons.colVis.js');
require('datatables.net-scroller');
require('style!datatables.net-scroller-bs/css/scroller.bootstrap.css');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
import {domButtonsScroller, formatBasicHeaders} from '../../../../utilities/datatables.utilities';

const createTable = (template, data) => {
  const columns = formatBasicHeaders(template);

  $('#linelist').DataTable({
    data,
    columns,
    dom: domButtonsScroller,
    buttons: [
      'colvis'
    ],
    scrollX: true,
    scrollY: 600,
    deferRender: true,
    scroller: true
  });
};

const template = `
<h2>Line List</h2>
<table id='linelist' 
    class='table table-striped' 
    cellspacing='0' width='100%'>
</table>
`;

const linelist = {
  template,
  controller($q, templateService, linelistService) {
    const promises = [];
    promises.push(templateService.getTemplate());
    promises.push(linelistService.getMetadata());

    $q
      .all(promises)
      .then(results => {
        const template = results[0].data.template;
        const data = results[1].data.metadata;
        createTable(template, data);
      });
  }
};

export default linelist;
