import angular from "angular";
import "../../../../../vendor/plugins/angular/angular-bootstrap-switch";
import "angular-aside";
import "angular-aside/dist/css/angular-aside.css";
import { MetadataButton } from "./metadata.button.component";
import { MetadataComponent } from "./metadata.component";
import { MetadataService } from "./metadata.service";

export const MetadataModule = angular
  .module("irida.vis.metadata", ["ngAside", "frapontillo.bootstrap-switch"])
  .service("MetadataService", MetadataService)
  .component("metadataButton", MetadataButton)
  .component("metadataComponent", MetadataComponent).name;
