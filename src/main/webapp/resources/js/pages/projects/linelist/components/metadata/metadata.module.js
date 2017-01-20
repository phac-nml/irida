const angular = require('angular');
require('angular-aside');
require('style!./../../../../../../../node_modules/angular-aside/dist/css/angular-aside.min.css');
require('bootstrap-switch');
require('angular-bootstrap-switch');
require('style!./../../../../../../../node_modules/bootstrap-switch/dist/css/bootstrap3/bootstrap-switch.min.css');
import {MetadataComponent} from './metadata.component';

export const MetadataModule = angular
  .module('irida.linelist.metadata', ['ui.bootstrap', 'ngAside', 'frapontillo.bootstrap-switch'])
  .component('metadataComponent', MetadataComponent)
  .name;
