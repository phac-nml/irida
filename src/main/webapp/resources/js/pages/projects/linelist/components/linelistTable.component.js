/* eslint new-cap: [2, {"capIsNewExceptions": ["DataTable"]}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
import {dom} from '../../../../constants/datatables.constants';

const createDataTable = url => {
  $('#linelist').DataTable({
    dom,
    ajax: url
  });
};

const template = `
<table id="linelist" 
    class="table table-striped" 
    cellspacing="0" width="100%"></table>
`;

const linelistTable = {
  template,
  bindings: {
    url: '@'
  },
  controller() {
    createDataTable(this.url);
  }
};

export default linelistTable;
