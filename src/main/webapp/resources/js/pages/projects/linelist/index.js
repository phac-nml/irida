require('./linelist.datatables');
const angular = require('angular');
import {MetadataModule} from './components/metadata/metadata.module';

const app = angular.module('irida');
app.requires.push(MetadataModule);
