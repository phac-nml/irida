/* eslint new-cap: ["error", { "capIsNewExceptions": ["DataTable"] }]*/
const $ = require('jquery');
const moment = require('moment');
require('timeago');
require('./../../vendor/datatables/datatables');
require('./../../vendor/datatables/datatables-buttons');

// Column look-ups for quick referencing
const COLUMNS = {
  ID: 0,
  NAME: 1,
  ORGANISM: 2,
  SAMPLES: 3,
  CREATED: 4,
  MODIFIED: 5
};

/**
 * Download table in specified format.
 * @param {string} format format of downloaded doc.
 */
function downloadItem({format = 'xlsx'}) {
  const url = `${window.PAGE.urls.export}&dtf=${format}`;
  const anchor = document.createElement('a');
  anchor.style.display = 'none';
  anchor.href = url;
  anchor.click();
}

if (typeof window.PAGE === 'object') {
  $('#projects').DataTable({
    // Table layout
    // Buttons / Filter
    // Table
    // Length / Paging / Info
    dom: `
<".row"
  <".col-md-8.buttons"B><".col-md-4"f>>
rt
<".row"<".col-md-3"l><".col-md-6"p><".col-md-3"i>>`,
    // Set up the export buttons.
    // These are loaded through the PAGE object.
    buttons: [
      {
        extend: 'collection',
        text() {
          return document.querySelector('#export-btn-text').innerHTML;
        },
        // The buttons are loaded onto the PAGE variable.
        buttons: window.PAGE.buttons.map(button => ({
          text: button.name,
          action() {
            downloadItem({format: button.format});
          }
        }))
      }
    ],
    processing: true,
    serverSide: true,
    ajax: window.PAGE.urls.projects,
    order: [[COLUMNS.MODIFIED, "desc"]],
    columnDefs: [
      {
        targets: [COLUMNS.NAME],
        render: function(data, type, full) {
          return `
<a class="btn btn-link" href="${window.PAGE.urls.project}${full.id}">${data}</a>
`;
        }
      },
      {
        targets: [COLUMNS.CREATED, COLUMNS.MODIFIED],
        render: function(data) {
          // Format the time (using timeago.js) to get the amount of time
          // since the event occurred.
          const date = moment(data);
          return `
<time data-toggle="tooltip" data-placement="top" 
      title="${date.toISOString()}">${$.timeago(date.toISOString())}</time>
`;
        }
      }
    ],
    createdRow: function(row) {
      const $row = $(row);
      $row.tooltip({selector: '[data-toggle="tooltip"]'});
    }
  });
}
