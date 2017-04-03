const angular = require('angular');
require('angular-messages');
require('ui-select');
require('style!ui-select/dist/select.css');
require('style!select2/dist/css/select2.css');
require('angular-drag-and-drop-lists');
import {showValidation} from '../../../../../directives/showValidation';
import {SampleMetadataTemplateModule} from '../../../common/sample-metadata-templates/sample-metadata-template.module';
import {deleteTemplate} from './deleteTemplate';
import {addMetadataField} from './addMetadataField';
import {MetadataFieldService} from './MetadataFieldService';
import {createTemplate} from './create-template.component';

export const CreateSampleMetadataTemplateModule = angular
  .module('irida.metadata.template', [
    SampleMetadataTemplateModule,
    'ngMessages',
    'ui.select',
    'dndLists'
  ])
  .service('MetadataFieldService', MetadataFieldService)
  .factory('deleteTemplate', deleteTemplate)
  .factory('addMetadataField', addMetadataField)
  .directive('showValidation', showValidation)
  .component('createTemplate', createTemplate)
  .name;
