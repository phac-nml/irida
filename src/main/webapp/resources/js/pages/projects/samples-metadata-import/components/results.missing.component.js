/* eslint new-cap: [2, {"capIsNewExceptions": ["DataTable"]}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
import {dom, formatBasicHeaders} from '../../../../constants/datatables.constants';

const resultsMissingComponent = {
  templateUrl: 'results.missing.component.tmpl.html',
  bindings: {
    rows: '=',
    headers: '='
  },
  controller() {
    const columns = formatBasicHeaders(this.headers);
    $('#missing-table').DataTable({
      dom,
      scrollX: true,
      sScrollX: '100%',
      data: this.rows,
      columns
    });
  }
};

export default resultsMissingComponent;
