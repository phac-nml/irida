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
import {domButtonsScroller, formatBasicHeaders, getDefaultTable}
  from '../../../../utilities/datatables.utilities';

const createTable = (template, data) => {
  const columns = formatBasicHeaders(template);

  if ($.fn.DataTable.isDataTable('#linelist')) {
    $('#linelist').DataTable().destroy();
    $('#linelist').empty();
  }

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

const linelist = {
  template: getDefaultTable('linelist'),
  controller($q, $scope, templateService, linelistService) {
    const generate = (templateName = 'default') => {
      const promises = [];
      promises.push(templateService.getTemplate(templateName));
      promises.push(linelistService.getMetadata(templateName));

      $q
        .all(promises)
        .then(results => {
          const template = results[0].data.template;
          const data = results[1].data.metadata;
          createTable(template, data);
        });
    };

    generate();

    $scope.$on('LINELIST_TEMPLATE_CHANGE', (event, args) => {
      generate(args.template);
    });
  }
};

export default linelist;
