import angular from "angular";
import { TREE } from "./../../constants";

/**
 * Angular controller for export phylocanvas as SVG
 * @param {object} $rootScope angularjs dom root element
 */
function exportSVGController($rootScope) {
  /**
   * Click handler for exporting the phylocanvas to svg file.
   */
  this.exportSVG = function() {
    $rootScope.$broadcast(TREE.EXPORT_SVG);
  };
}

exportSVGController.$inject = ["$rootScope"];

const exportSVGComponent = {
  templateUrl: "export-svg-component.tmpl.html",
  controller: exportSVGController
};

export const ExportSVG = angular
  .module("irida.vis.exportSVG", [])
  .component("exportSvgComponent", exportSVGComponent).name;
