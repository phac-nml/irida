const angular = require('angular');
import uiRouter from 'angular-ui-router';
import {states} from './router/router.config';
import dropzone from '../../../directives/dropzone';
import metadataUploader from './components/metadataUploader';
import displayColumnHeaders from './components/displayColumnHeaders';
import headerItem from './components/headerItem';
import resultsTable from './components/resultsTable';
import {sampleMetadataService} from './factories/metadataImport.service';

const app = angular.module('irida');
app.requires.push(uiRouter);
app
  .config(states)
  .service('sampleMetadataService', sampleMetadataService)
  .directive('dropzone', dropzone)
  .component('metadataUploader', metadataUploader)
  .component('displayColumnHeaders', displayColumnHeaders)
  .component('headerItem', headerItem)
  .component('resultsTable', resultsTable);
