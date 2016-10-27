const angular = require('angular');
import {PhylocanvasModule} from './components/phylocanvas/phylocanvas.module';

const app = angular.module('irida');

app.requires.push(PhylocanvasModule);
