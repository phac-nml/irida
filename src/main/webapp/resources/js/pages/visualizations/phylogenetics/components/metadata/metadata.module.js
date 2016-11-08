const angular = require('angular');
import {MetadataComponent} from './metadata.component';
import {MetadataService} from './metadata.service';

export const MetadataModule = angular
  .module('irida.vis.metadata', [])
  .service('MetadataService', MetadataService)
  .component('metadataComponent', MetadataComponent)
  .name;
