const angular = require('angular');
import {PhylocanvasComponent} from './phylocanvas.component';
import {PhylocanvasService} from './phylocanvas.service';

export const PhylocanvasModule = angular
  .module('irida.vis.phylocanvas', [])
  .component('phylocanvasComponent', PhylocanvasComponent)
  .service('PhylocanvasService', PhylocanvasService)
  .name;
