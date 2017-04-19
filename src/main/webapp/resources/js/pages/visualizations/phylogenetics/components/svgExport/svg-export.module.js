const angular = require('angular');
import {TREE} from './../../constants';

class ExportSVGController {
  constructor($rootScope) {
    this.rootScope = $rootScope;
  }

  exportSVG() {
    this.rootScope.$broadcast(TREE.EXPORT_SVG);
  }
}

ExportSVGController.$inject = [
  '$rootScope'
];

const ExportSVGComponent = {
  templateUrl: 'export-svg-component.tmpl.html',
  controller: ExportSVGController
};

export const ExportSVG = angular
  .module('irida.vis.exportSVG', [])
  .component('exportSvgComponent', ExportSVGComponent)
  .name;
