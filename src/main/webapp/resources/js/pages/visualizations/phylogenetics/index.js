const angular = require('angular');
import {TemplateSelectorModule} from './components/templateSelector/templateSelector.module';
import {PhylocanvasModule} from './components/phylocanvas/phylocanvas.module';
import {MetadataModule} from './components/metadata/metadata.module';

const app = angular.module('irida');

app.requires.push(TemplateSelectorModule);
app.requires.push(PhylocanvasModule);
app.requires.push(MetadataModule);
