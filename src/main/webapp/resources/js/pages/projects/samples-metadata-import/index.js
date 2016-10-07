const angular = require('angular');
import uiRouter from 'angular-ui-router';
import {states} from './router/router.config';
import dropzone from '../../../directives/dropzone';
import metadataUploader from './components/upload.component';
import selectSampleNameColumnComponent from './components/selectSampleNameColumn.component';
import headerItem from './components/headerItem.component';
import resultsComponent from './components/results.component';
import saveMetadata from './components/saveMetadata.component';
import resultsFoundComponent from './components/results.found.component';
import resultsMissingComponent from './components/results.missing.component';
import {sampleMetadataService} from './factories/metadataImport.service';

const app = angular.module('irida');

// ui.router is not on the loaded by default so we need to inject it into angular here.
app.requires.push(uiRouter);
app
  .config(states)
  .service('sampleMetadataService', sampleMetadataService)
  .directive('dropzone', dropzone)
  .component('metadataUploader', metadataUploader)
  .component('selectSampleNameColumnComponent', selectSampleNameColumnComponent)
  .component('headerItem', headerItem)
  .component('resultsComponent', resultsComponent)
  .component('saveMetadata', saveMetadata)
  .component('resultsFoundComponent', resultsFoundComponent)
  .component('resultsMissingComponent', resultsMissingComponent);
