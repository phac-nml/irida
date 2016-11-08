/* eslint new-cap: [2, {'capIsNewExceptions': ['DataTable']}] */
const $ = require('jquery');
require('datatables.net');
require('datatables-bootstrap3-plugin');
require('datatables.net-buttons');
require('datatables.net-buttons-bs');
require('datatables.net-buttons/js/buttons.colVis.js');
require('datatables.net-scroller');
require('style!datatables.net-scroller-bs/css/scroller.bootstrap.css');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');
import {domButtonsScroller, formatBasicHeaders, getDefaultTable} from '../../../../../utilities/datatables.utilities';

const TABLE_ID = 'linelist';

const createTable = (template, data) => {
  const $table = $(`#${TABLE_ID}`);
  const columns = formatBasicHeaders(template);

  // If the table is already initialized it needs to be destroyed before
  // it can be loaded again.
  if ($.fn.DataTable.isDataTable(`#${TABLE_ID}`)) {
    $table.DataTable().destroy();
    $table.empty();
  }

  $table.DataTable({
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

export const LinelistComponent = {
  bindings: {
    url: '@',     // url to get the metadata from
    template: '@' // identifier for the current template.
  },
  template: getDefaultTable('linelist'),
  controller($q, $scope, LinelistService) {
    /**
     * Generate the line list table.
     * @param {string} templateName name of the table header template
     */
    const generate = templateName => {
      const promises = [];
      promises.push(LinelistService.getTemplate(this.url, templateName));
      promises.push(LinelistService.getMetadata(this.url, templateName));

      $q
        .all(promises)
        .then(results => {
          const template = results[0];
          const data = results[1];
          createTable(template, data);
        });
    };

    // Initialize the table.
    generate(this.template);

    /**
     * Listen for a change in the template to reload the table.
     */
    $scope.$on('LINELIST_TEMPLATE_CHANGE', (event, args) => {
      generate(args.template);
    });
  }
};
