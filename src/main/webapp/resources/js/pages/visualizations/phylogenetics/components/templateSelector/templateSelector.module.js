const angular = require('angular');
import {TemplateSelectorComponent} from './templateSelector.component';

export const TemplateSelectorModule = angular
  .module('irida.vis.templateSelector', [])
  .component('templateSelectorComponent', TemplateSelectorComponent)
  .name;
