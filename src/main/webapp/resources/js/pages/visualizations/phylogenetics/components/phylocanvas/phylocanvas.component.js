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
 * @param {object} PhylocanvasService angular service for server exchanges
 */
function controller($window, PhylocanvasService) {
  setCanvasHeight($window);

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

  tree.on('beforeFirstDraw', function() {
    tree.leaves.forEach(leaf => {
      leaf.data = {
        column1: {
          colour: '#3C7383',
          label: leaf.label
        },
        column2: '#9BB7BF',
        column3: '#3C7383',
        column4: '#9BB7BF'
      };
    });
  });

  let newick;
  PhylocanvasService.getNewickData(this.newick)
    .then(data => {
      newick = data;
      tree.load(newick);
      // console.log(tree.exportSVG.getSerialisedSVG());
    });
}

export const PhylocanvasComponent = {
  bindings: {
    newick: '@'
  },
  templateUrl: 'phylocanvas.tmpl.html',
  controller
};
