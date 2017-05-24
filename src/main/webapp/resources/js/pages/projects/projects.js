/* eslint new-cap: ["error", { "capIsNewExceptions": ["DataTable"] }]*/
const $ = require('jquery');
const moment = require('moment');

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
    dom: `
<".row"
  <".col-md-8.buttons"B><".col-md-4"f>>
rt
<".row"<".col-md-3"l><".col-md-6"p><".col-md-3"i>>`,
    buttons: [
      // Set up the export buttons (part of DataTables std buttons);
      {
        extend: 'collection',
        text: `
<i class="fa fa-download" aria-hidden="true"></i>
    &nbsp;${window.PAGE.i18n.exportBtn}&nbsp;&nbsp;
    <i class="fa fa-caret-down" aria-hidden="true">
</i>`,
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
          const date = moment(data);
          return `
<span data-toggle="tooltip" data-placement="top" 
      title="${date.toISOString()}" data-livestamp="${date.unix()}">
      <i class="fa fa-spinner fa-pulse fa-fw"></i>
</span>
`;
        }
      }
    ],
    createdRow: function(row) {
      $(row).tooltip({selector: '[data-toggle="tooltip"]'});
    }
  });
}
