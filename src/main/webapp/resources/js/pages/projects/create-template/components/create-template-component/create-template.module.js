const angular = require('angular');
require('angular-messages');
import {SampleMetadataTemplateModule} from '../../../common/sample-metadata-templates/sample-metadata-template.module';
import {showValidation} from '../../../../../directives/showValidation';
import {createTemplate} from './create-template.component';

export const CreateSampleMetadataTemplateModule = angular
  .module('irida.metadata.template', [SampleMetadataTemplateModule, 'ngMessages'])
  .directive('showValidation', showValidation)
  .component('createTemplate', createTemplate)
  .name;
