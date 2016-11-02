const angular = require('angular');
import {TemplateInputModule} from './components/templateInput/templateInput.module';
const app = angular.module('irida');
app.requires.push(TemplateInputModule);
