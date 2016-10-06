/* eslint new-cap: [2, {"capIsNewExceptions": ["DataTable"]}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
import {dom, formatBasicHeaders} from '../../../../constants/datatables.constants';

const resultsFoundComponent = {
  templateUrl: 'results.found.component.tmpl.html',
  bindings: {
    rows: '=',
    headers: '='
  },
  controller() {
    const columns = formatBasicHeaders(this.headers);
    $('#found-table').DataTable({
      dom,
      scrollX: true,
      sScrollX: '100%',
      data: this.rows,
      columns
    });
  }
};

export default resultsFoundComponent;
