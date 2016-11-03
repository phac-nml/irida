const angular = require('angular');
import {MetadataService} from './metadata.service';

export const MetadataModule = angular
  .module('irida.vis.metadata', [])
  .service('MetadataService', MetadataService)
  .name;
