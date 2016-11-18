const angular = require('angular');
require('angular-drag-and-drop-lists');
const ngMessages = require('angular-messages');
import {Select2BasicModule} from './../../../../../modules/select2/select2basic.module';
import {TemplateInputService} from './templateInput.service';
import {TemplateInputComponent} from './templateInput.component';
import {noRepeatName} from './templateInput.directives';

export const TemplateInputModule = angular
  .module('irida.projects.template', ['dndLists', ngMessages, Select2BasicModule])
  .directive('noRepeatName', noRepeatName)
  .component('templateInputComponent', TemplateInputComponent)
  .service('TemplateInputService', TemplateInputService)
  .name;
