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
    let count = 0;

    $('#results-table').DataTable({
      scrollX: true,
      data: details.table,
      columns: mapHeaders(details.headers),
      createdRow(row, data, index) {
        if (!data.identifier) {
          $(row).addClass('bg-danger');
          count++;
        }
      }
    });

    this.missing = count;
  }
};

export default resultsTable;
