const angular = require('angular');
const ngAside = require('angular-aside');
import {MetadataService} from './metadata.service';
import {MetadataComponent} from './metadata.component';

export const MetadataModule = angular
  .module('irida.vis.metadata', [ngAside])
  .service('MetadataService', MetadataService)
  .component('metadataComponent', MetadataComponent)
  .name;
