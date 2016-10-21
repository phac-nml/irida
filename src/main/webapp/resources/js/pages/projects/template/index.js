const angular = require('angular');
import TemplateService from './template.service';
import Select2Basic from './../../../directives/select2/select2-basic.directive';
import templateInput from './components/templateInput.component';

const app = angular.module('irida');

app
  .service('TemplateService', TemplateService)
  .directive('select2Basic', Select2Basic)
  .component('templateInput', templateInput);
