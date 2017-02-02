const angular = require('angular');
import {LinelistTable} from './components/linelist-table/linelist-table.module';
import {MetadataModule} from './components/linelist-metadata/linelist-metadata.module';

export const LinelistModule = angular
  .module('irida.linelist', [
    'ui.bootstrap',
    LinelistTable,
    MetadataModule
  ])
  .name;
