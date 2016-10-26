const angular = require('angular');
import {PhylocanvasModule} from './components/phylocanvas/phylocanvas.module';
import {ToolbarModule} from './components/toolbar/toolbar.module';

const app = angular.module('irida');

app.requires.push(PhylocanvasModule);
app.requires.push(ToolbarModule);
