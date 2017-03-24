const angular = require('angular');
require('angular-messages');
require('ui-select');
require('style!ui-select/dist/select.css');
require('style!select2/dist/css/select2.css');
import {showValidation} from '../../../../../directives/showValidation';
import {SampleMetadataTemplateModule} from '../../../common/sample-metadata-templates/sample-metadata-template.module';
import {addMetadataField} from './addMetadataField';
import {MetadataFieldService} from './MetadataFieldService';
import {createTemplate} from './create-template.component';

export const CreateSampleMetadataTemplateModule = angular
  .module('irida.metadata.template', [
    SampleMetadataTemplateModule,
    'ngMessages',
    'ui.select'
  ])
  .service('MetadataFieldService', MetadataFieldService)
  .factory('addMetadataField', addMetadataField)
  .directive('showValidation', showValidation)
  .component('createTemplate', createTemplate)
  .name;
