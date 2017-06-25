import angular from 'angular';
import 'DataTables/datatables.js';
import 'DataTables/datatables-scroller.js';

import datatables from 'angular-datatables';
import datatablesScroller from 'angular-datatables/dist/plugins/scroller/angular-datatables.scroller.min.js';
import datatablesColReorder from 'angular-datatables/dist/plugins/colreorder/angular-datatables.colreorder.min.js';

import {LinelistService} from '../../services/linelist.service';
import {TableComponent} from './linelist-table.component';

export const LinelistTable = angular
  .module('irida.linelist.table', [
    datatables,
    datatablesScroller,
    datatablesColReorder
  ])
  .service('LinelistService', LinelistService)
  .component('linelistTable', TableComponent)
  .name;
