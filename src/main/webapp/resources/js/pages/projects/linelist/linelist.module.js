const angular = require('angular');
import {LinelistTable} from './components/linelist-table/linelist-table.module';
import {Linelist} from './linelist.component';
import {LinelistStore} from './LinelistStore';
import {LinelistService} from './linelist.service';
import {MetadataModule} from './components/linelist-metadata/linelist-metadata.module';

export const LinelistModule = angular
  .module('irida.linelist', [
    'ui.bootstrap',
    LinelistTable,
    MetadataModule
  ])
  .factory('LinelistStore', LinelistStore)
  .service('LinelistService', LinelistService)
  .component('linelist', Linelist)
  .name;
