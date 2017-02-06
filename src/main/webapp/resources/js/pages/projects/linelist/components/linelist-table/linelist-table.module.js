const angular = require('angular');
require('datatables.net');
const datatables = require('angular-datatables');
const datatablesScroller = require('angular-datatables/dist/plugins/scroller/angular-datatables.scroller.min.js');

require('datatables-bootstrap3-plugin');
require('datatables.net-scroller');
require('style!datatables.net-scroller-bs/css/scroller.bootstrap.css');
require('style!datatables-bootstrap3-plugin/media/css/datatables-bootstrap3.css');

import {LinelistService} from '../../linelist.service';
import {TableComponent} from './linelist-table.component';

export const LinelistTable = angular
  .module('irida.linelist.table', [
    datatables,
    datatablesScroller
  ])
  .service('LinelistService', LinelistService)
  .component('linelistTable', TableComponent)
  .name;
