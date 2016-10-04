/* eslint new-cap: [2, {"capIsNewExceptions": ["DataTable"]}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');

const mapHeaders = headers => {
  return headers.map(title => {
    return {title, data: title};
  });
};

const resultsTable = {
  templateUrl: 'resultsTable.tmpl.html',
  controller(sampleMetadataService) {
    const details = sampleMetadataService.getProjectData();
    const headers = mapHeaders(details.headers);

    this.found = details.tables.goodRows.length;
    this.missing = details.tables.badRows.length;

    if (this.found > 0) {
      $('#results-table').DataTable({
        scrollX: true,
        data: details.tables.goodRows,
        columns: headers
      });
    }

    if (this.missing > 0) {
      $('#ignored-table').DataTable({
        scrollX: true,
        data: details.tables.badRows,
        columns: headers
      });
    }

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
