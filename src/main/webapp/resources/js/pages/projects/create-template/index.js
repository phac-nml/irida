const angular = require('angular');
import {CreateSampleMetadataTemplateModule} from './components/create-template-component/create-template.module';

const app = angular.module('irida');
app.requires.push(CreateSampleMetadataTemplateModule);
