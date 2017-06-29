import angular from 'angular';
import {LinelistTable} from './components/linelist-table/linelist-table.module';
import {Linelist} from './linelist.component';
import {LinelistService} from './services/linelist.service';
import {MetadataModule} from './components/linelist-metadata/linelist-metadata.module';

export const LinelistModule = angular
  .module('irida.linelist', [
    'ui.bootstrap',
    LinelistTable,
    MetadataModule
  ])
  .service('LinelistService', LinelistService)
  .component('linelist', Linelist)
  .name;
