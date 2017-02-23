const angular = require('angular');
import {LinelistTable} from './components/linelist-table/linelist-table.module';
import {Linelist} from './linelist.component';
import {MetadataTemplateService} from './services/metadata-template.service';
import {LinelistService} from './services/linelist.service';
import {MetadataModule} from './components/linelist-metadata/linelist-metadata.module';

export const LinelistModule = angular
  .module('irida.linelist', [
    'ui.bootstrap',
    LinelistTable,
    MetadataModule
  ])
  .service('LinelistService', LinelistService)
  .service('MetadataTemplateService', MetadataTemplateService)
  .component('linelist', Linelist)
  .name;
