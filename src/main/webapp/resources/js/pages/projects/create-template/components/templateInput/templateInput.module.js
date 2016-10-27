const angular = require('angular');
import {Select2BasicModule} from './../../../../../modules/select2/select2basic.module';
import {TemplateInputService} from './templateInput.service';
import {TemplateInputComponent} from './templateInput.component';

export const TemplateInputModule = angular
  .module('irida.projects.template', [Select2BasicModule])
  .component('templateInputComponent', TemplateInputComponent)
  .service('TemplateInputService', TemplateInputService)
  .name;
