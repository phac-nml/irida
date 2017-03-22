const angular = require('angular');
require('angular-drag-and-drop-lists');
require('ui-select');
require('style!ui-select/dist/select.css');
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
