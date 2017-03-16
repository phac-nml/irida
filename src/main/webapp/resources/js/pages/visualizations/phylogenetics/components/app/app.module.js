const angular = require('angular');
import {AppComponent} from './app.component.js';

export const AppModule = angular
  .module('irida.vis.errors', [])
  .component('appComponent', AppComponent)
  .name;
