/* eslint new-cap: [2, {"capIsNewExceptions": ["DataTable"]}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
import {dom} from '../../../../constants/datatables.constants';

const mapHeaders = headers => {
  return headers.map(title => {
    return {title, data: title};
  });
};

const resultsTable = {
  templateUrl: 'resultsTable.tmpl.html',
  bindings: {
    data: '='
  },
  controller(sampleMetadataService) {
    const columns = mapHeaders(this.data.headers);
    this.found = this.data.found.length;
    this.missing = this.data.missing.length;

    let goodTable;
    this.loadGoodRows = () => {
      if (!goodTable) {
        goodTable = $('#results-table').DataTable({
          dom,
          scrollX: true,
          data: this.data.found,
          columns
        });
      }
    };

    let badTable;
    this.loadBadRows = () => {
      if (!badTable) {
        badTable = $('#ignored-table').DataTable({
          dom,
          scrollX: true,
          data: this.data.missing,
          columns
        });
      }
    };

    this.saveMetadata = () => {
      sampleMetadataService
        .saveMetadata()
        .then(result => {
          console.log(result);
        });
    };
  }
};

export default resultsTable;
