const angular = require('angular');
require('bootstrap-switch');
require('style!bootstrap-switch/dist/css/bootstrap3/bootstrap-switch.css');
require('angular-bootstrap-switch');
require('angular-aside');
require('style!angular-aside/dist/css/angular-aside.css');
import {MetadataButton} from './metadata.button.component';
import {MetadataComponent} from './metadata.component';
import {MetadataService} from './metadata.service';

export const MetadataModule = angular
  .module('irida.vis.metadata', [
    'ngAside',
    'frapontillo.bootstrap-switch'
  ])
  .service('MetadataService', MetadataService)
  .component('metadataButton', MetadataButton)
  .component('metadataComponent', MetadataComponent)
  .name;
