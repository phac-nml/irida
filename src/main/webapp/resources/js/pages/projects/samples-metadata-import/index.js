const angular = require('angular');
import uiRouter from 'angular-ui-router';
import {states} from "./router/config";
import dropzone from "../../../directives/dropzone";
import {sampleMetadataService} from "./factories/metadataImportService";

const app = angular.module('irida');
app.requires.push(uiRouter);
app
  .config(states)
  .directive("dropzone", dropzone)
  .service("sampleMetadataService", sampleMetadataService);
