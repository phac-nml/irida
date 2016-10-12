const angular = require('angular');
import linelistService from './factories/linelist.service';
import linelistTable from './components/linelistTable.component';

const app = angular.module('irida');

app
  .factory('linelistService', ['$http', linelistService])
  .component('linelistTable', linelistTable);
