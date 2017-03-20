const angular = require('angular');
require('angular-resource');
require('angular-messages');
require('angular-aside');
require('style!./../../../../../../../node_modules/angular-aside/dist/css/angular-aside.min.css');
require('bootstrap-switch');
require('angular-bootstrap-switch');
require('style!./../../../../../../../node_modules/bootstrap-switch/dist/css/bootstrap3/bootstrap-switch.min.css');
import {MetadataTemplateService} from '../../services/metadata-template.service';
import {MetadataComponent} from './linelist-metadata.component';
import {metadataTemplateName} from '../../../common/sample-metadata-templates/directives/metadataTemplateName';

export const MetadataModule = angular
  .module('irida.linelist.metadata', [
    'ngResource',
    'ngMessages',
    'ui.bootstrap',
    'ngAside',
    'frapontillo.bootstrap-switch'
  ])
  .directive('metadataTemplateName', metadataTemplateName)
  .service('MetadataService', MetadataTemplateService)
  .component('metadataComponent', MetadataComponent)
  .name;
