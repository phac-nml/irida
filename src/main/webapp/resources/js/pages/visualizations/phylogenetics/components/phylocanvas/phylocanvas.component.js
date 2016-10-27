import Phylocanvas from 'phylocanvas';
import metadataPlugin from 'phylocanvas-plugin-metadata';
import exportSvgPlugin from 'phylocanvas-plugin-export-svg';
import {Colours} from './../../../../../utilities/colour.utilities';

const PHYLOCANVAS_DIV = 'phylocanvas';

Phylocanvas.plugin(metadataPlugin);
Phylocanvas.plugin(exportSvgPlugin);

const setCanvasHeight = $window => {
  const canvas = document.querySelector(`#${PHYLOCANVAS_DIV}`);
  canvas.style.height = `${$window.innerHeight - 200}px`;
};

const colourMap = {};
const generateColourMap = data => {
  const colours = {};
  const labels = Object.keys(data);
  for (const label of labels) {
    const terms = Object.keys(data[label]);
    for (const term of terms) {
      colours[term] = colours[term] ? colours[term] : new Colours();
      colourMap[term] = colourMap[term] ? colourMap[term] : {};
      if (!colourMap[term][data[label][term]]) {
        colourMap[term][data[label][term]] = colours[term].getNext();
      }
    }
  }
};

/**
 * Angular controller function for this scope.
 * @param {object} $window AngularJS window object
 * @param {object} PhylocanvasService angular service for server exchanges
 */
function controller($window, PhylocanvasService) {
  setCanvasHeight($window);

  console.log(this);

  const tree = Phylocanvas.createTree(PHYLOCANVAS_DIV, {
    metadata: {
      showHeaders: true,
      showLabels: true,
      blockLength: 32,
      blockSize: 32,
      padding: 18,
      columns: [],
      propertyName: 'data',
      underlineHeaders: true,
      headerAngle: 0,
      fillStyle: 'black',
      strokeStyle: 'black',
      lineWidth: 1,
      font: null
    }
  });
  tree.setTreeType('rectangular');
  tree.alignLabels = true;

  const loadMetadata = metadata => {
    tree.on('beforeFirstDraw', () => {
      let prev;
      tree.leaves.forEach(leaf => {
        const md = metadata[leaf.label] || prev;
        delete md.Comments;
        Object.keys(md).forEach(key => {
          console.log(key, md[key], colourMap[key][md[key]]);
          md[key] = {colour: colourMap[key][md[key]], label: md[key]};
        });
        leaf.data = md;
        prev = Object.assign({}, prev);
      });
    });
  };

  PhylocanvasService.getMetadata(this.metadataurl)
    .then(data => {
      generateColourMap(data);
      loadMetadata(data);
      PhylocanvasService.getNewickData(this.newickurl)
        .then(data => {
          tree.load(data);
          console.log('tree loaded');
        });
    });
}

export const PhylocanvasComponent = {
  bindings: {
    newickurl: '@',
    metadataurl: '@'
  },
  templateUrl: 'phylocanvas.tmpl.html',
  controller
};
