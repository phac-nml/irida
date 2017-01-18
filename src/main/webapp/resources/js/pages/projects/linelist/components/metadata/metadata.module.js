const angular = require('angular');
require('angular-aside');
require('style!./../../../../../../../node_modules/angular-aside/dist/css/angular-aside.min.css');
import {MetadataComponent} from './metadata.component';

export const MetadataModule = angular
  .module('irida.linelist.metadata', ['ui.bootstrap', 'ngAside'])
  .component('metadataComponent', MetadataComponent)
  .name;
