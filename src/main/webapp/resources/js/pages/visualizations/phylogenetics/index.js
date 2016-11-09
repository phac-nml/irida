const angular = require('angular');
import {MetadataModule} from './components/metadata/metadata.module';
import {PhylocanvasModule} from './components/phylocanvas/phylocanvas.module';
import {TemplateModule} from './components/templates/template.module';

const app = angular.module('irida');

app.requires.push(MetadataModule);
app.requires.push(PhylocanvasModule);
app.requires.push(TemplateModule);
