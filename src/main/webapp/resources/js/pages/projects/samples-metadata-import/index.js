import angular from "angular";
import uiRouter from "angular-ui-router";
import ngFileUpload from "ng-file-upload";
import { states } from "./router.config";
import metadataUploader from "./components/upload.component";
import selectSampleNameColumnComponent
  from "./components/selectSampleNameColumn.component";
import headerItem from "./components/headerItem.component";
import resultsComponent from "./components/results.component";
import saveMetadata from "./components/saveMetadata.component";
import resultsFoundComponent from "./components/results.found.component";
import resultsMissingComponent from "./components/results.missing.component";
import { sampleMetadataService } from "./factories/metadataImport.service";
import "../../../../css/pages/project-samples-metadata-import.css";

angular
  .module("irida.metadata.importer", [uiRouter, ngFileUpload])
  .config(["$stateProvider", "$urlRouterProvider", states])
  .service("sampleMetadataService", [
    "$http",
    "$window",
    "Upload",
    sampleMetadataService,
  ])
  .component("metadataUploader", metadataUploader)
  .component("selectSampleNameColumnComponent", selectSampleNameColumnComponent)
  .component("headerItem", headerItem)
  .component("resultsComponent", resultsComponent)
  .component("saveMetadata", saveMetadata)
  .component("resultsFoundComponent", resultsFoundComponent)
  .component("resultsMissingComponent", resultsMissingComponent);
