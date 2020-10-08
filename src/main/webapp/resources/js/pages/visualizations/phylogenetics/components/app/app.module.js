import angular from "angular";
import uiBootstrap from "angular-ui-bootstrap";
import { MetadataModule } from "./../metadata/metadata.module";
import { PhylocanvasModule } from "./../phylocanvas/phylocanvas.module";
import { TemplateModule } from "./../templates/template.module";
import { AppComponent } from "./app.component.js";
import { ExportSVG } from "./../svgExport/svg-export.module";

export const AppModule = angular
  .module("irida.vis.errors", [
    MetadataModule,
    PhylocanvasModule,
    TemplateModule,
    ExportSVG,
    uiBootstrap
  ])
  .component("appComponent", AppComponent).name;
