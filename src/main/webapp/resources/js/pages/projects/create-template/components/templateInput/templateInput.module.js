const angular = require('angular');

require('angular-drag-and-drop-lists');
const ngMessages = require('angular-messages');
import {metadataSelect2} from './../metadataSelect2';
import {TemplateInputService} from './templateInput.service';
import {TemplateInputComponent} from './templateInput.component';
import {noRepeatName} from './templateInput.directives';

export const TemplateInputModule = angular
  .module('irida.projects.template', ['dndLists', ngMessages])
  .directive('metadataSelect2', metadataSelect2)
  .directive('noRepeatName', noRepeatName)
  .component('templateInputComponent', TemplateInputComponent)
  .service('TemplateInputService', TemplateInputService)
  .name;
