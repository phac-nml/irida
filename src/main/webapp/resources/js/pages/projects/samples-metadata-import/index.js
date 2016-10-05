const angular = require('angular');
import uiRouter from 'angular-ui-router';
import {states} from './router/router.config';
import dropzone from '../../../directives/dropzone';
import metadataUploader from './components/metadataUploader.component';
import selectSampleNameColumnComponent from './components/selectSampleNameColumn.component';
import headerItem from './components/headerItem.component';
import resultsTable from './components/results.component';
import {sampleMetadataService} from './factories/metadataImport.service';

const app = angular.module('irida');
app.requires.push(uiRouter);
app
  .config(states)
  .service('sampleMetadataService', sampleMetadataService)
  .directive('dropzone', dropzone)
  .component('metadataUploader', metadataUploader)
  .component('selectSampleNameColumnComponent', selectSampleNameColumnComponent)
  .component('headerItem', headerItem)
  .component('resultsTable', resultsTable);
