const angular = require('angular');
import {LinelistComponent} from './linelist.component';
import {LinelistService} from './linelist.service';

export const LineListModule = angular
  .module('irida.projects.linelist', [])
  .component('linelistComponent', LinelistComponent)
  .service('LinelistService', LinelistService)
  .name;
