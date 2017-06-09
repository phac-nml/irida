const angular = require('angular');
require('angular-messages');
require('angular-aside');
require('style!angular-aside/dist/css/angular-aside.min.css');
require('bootstrap-switch');
require('angular-bootstrap-switch');
require('style!bootstrap-switch/dist/css/bootstrap3/bootstrap-switch.min.css');
import {MetadataComponent} from './linelist-metadata.component';
import {SampleMetadataTemplateModule} from '../../../common/sample-metadata-templates/sample-metadata-template.module';

export const MetadataModule = angular
  .module('irida.linelist.metadata', [
    'ngMessages',
    'ui.bootstrap',
    'ngAside',
    'frapontillo.bootstrap-switch',
    'irida.notifications',
    SampleMetadataTemplateModule
  ])
  .component('metadataComponent', MetadataComponent)
  .name;
