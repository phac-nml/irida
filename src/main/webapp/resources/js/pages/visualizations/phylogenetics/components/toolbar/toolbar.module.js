const angular = require('angular');
import {ToolbarComponent} from './toolbar.component';

export const ToolbarModule = angular
  .module('irida.vis.toolbar', [])
  .component('toolbarComponent', ToolbarComponent)
  .name;
