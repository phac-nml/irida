import angular from "angular";
import "angular-messages";
import "../../../../../vendor/plugins/angular/angular-aside";
import "../../../../../vendor/plugins/angular/angular-bootstrap-switch";
import { MetadataComponent } from "./linelist-metadata.component";
import { SampleMetadataTemplateModule } from "../../../common/sample-metadata-templates/sample-metadata-template.module";

export const MetadataModule = angular
  .module('irida.linelist.metadata', [
    'ngMessages',
    'ui.bootstrap',
    'ngAside',
    'frapontillo.bootstrap-switch',
    SampleMetadataTemplateModule
  ])
  .component('metadataComponent', MetadataComponent)
  .name;
