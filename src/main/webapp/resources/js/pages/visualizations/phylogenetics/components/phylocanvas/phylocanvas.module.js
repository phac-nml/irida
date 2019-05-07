import angular from "angular";
import Phylocanvas from "phylocanvas";
import { PhylocanvasComponent } from "./phylocanvas.component";
import { PhylocanvasService } from "./phylocanvas.service";

export const PhylocanvasModule = angular
  .module("irida.vis.phylocanvas", [])
  .value("Phylocanvas", Phylocanvas)
  .component("phylocanvasComponent", PhylocanvasComponent)
  .service("PhylocanvasService", PhylocanvasService).name;
