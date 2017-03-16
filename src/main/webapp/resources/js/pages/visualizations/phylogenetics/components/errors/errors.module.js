const angular = require('angular');
import {ErrorsComponent} from './errors.component';

export const ErrorsModule = angular
  .module('irida.vis.errors', [])
  .component('errorsComponent', ErrorsComponent)
  .name;
