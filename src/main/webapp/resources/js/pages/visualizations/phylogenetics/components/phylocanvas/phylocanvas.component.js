import Phylocanvas from 'phylocanvas';
import metadataPlugin from 'phylocanvas-plugin-metadata';
import exportSvgPlugin from 'phylocanvas-plugin-export-svg';

const PHYLOCANVAS_DIV = 'phylocanvas';

Phylocanvas.plugin(metadataPlugin);
Phylocanvas.plugin(exportSvgPlugin);

const setCanvasHeight = $window => {
  const canvas = document.querySelector(`#${PHYLOCANVAS_DIV}`);
  canvas.style.height = `${$window.innerHeight - 200}px`;
};

/**
 * Angular controller function for this scope.
 * @param {object} $window AngularJS window object
 * @param {object} $q AngularJS promise object
 * @param {object} PhylocanvasService angular service for server exchanges
 */
function controller($window, $q, PhylocanvasService) {
  setCanvasHeight($window);

  console.log(this);

  const tree = Phylocanvas.createTree(PHYLOCANVAS_DIV, {
    metadata: {
      active: true,
      showHeaders: true,
      showLabels: true,
      blockLength: 32,
      blockSize: null,
      padding: 8,
      columns: [],
      propertyName: 'data',
      underlineHeaders: true,
      headerAngle: 90,
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
        console.log(leaf.label, md);
        Object.keys(md).forEach(key => {
          md[key] = {colour: 'rgba(55, 123, 181, 20)', label: md[key]};
        });
        leaf.data = md;
        prev = md;
      });
    });
  };

  PhylocanvasService.getMetadata(this.metadataurl)
    .then(data => {
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
