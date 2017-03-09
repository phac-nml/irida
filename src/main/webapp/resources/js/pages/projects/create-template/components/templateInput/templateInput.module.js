const angular = require('angular');

// Select
require('ui-select');
require('style!ui-select/dist/select.css');

require('angular-drag-and-drop-lists');
const ngMessages = require('angular-messages');
import {TemplateInputService} from './templateInput.service';
import {TemplateInputComponent} from './templateInput.component';
import {noRepeatName} from './templateInput.directives';

export const TemplateInputModule = angular
  .module('irida.projects.template', ['dndLists', 'ui.select', ngMessages])
  .directive('noRepeatName', noRepeatName)
  .component('templateInputComponent', TemplateInputComponent)
  .service('TemplateInputService', TemplateInputService)
  .name;
