import angular from "angular";
import { Linelist } from "./linelist.component";
import { LinelistService } from "./services/linelist.service";
import { MetadataModule } from "./components/linelist-metadata/linelist-metadata.module";
import { LineListTableController } from "./controllers/linelist-table.controller";

export const LinelistModule = angular
  .module("irida.linelist", ["ui.bootstrap", MetadataModule])
  .service("LinelistService", LinelistService)
  .component("linelist", Linelist)
  .controller("lineListTableController", LineListTableController).name;
