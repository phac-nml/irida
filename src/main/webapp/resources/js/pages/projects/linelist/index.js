const angular = require('angular');
import linelistService from './factories/linelist.service';
import templateService from './factories/template.service';
import linelist from './components/linelist.component';

const app = angular.module('irida');

app
  .service('linelistService', linelistService)
  .service('templateService', templateService)
  .component('linelist', linelist);
