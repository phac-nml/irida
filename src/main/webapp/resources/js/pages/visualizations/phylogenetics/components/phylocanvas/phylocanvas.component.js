import Phylocanvas from 'phylocanvas';
import metadataPlugin from 'phylocanvas-plugin-metadata';
import {METADATA} from './../../constants';

const PHYLOCANVAS_DIV = 'phylocanvas';

Phylocanvas.plugin(metadataPlugin);

const metadataFormat = {
  showHeaders: true,
  showLabels: true,
  blockLength: 32,
  blockSize: 32,
  padding: 25,
  propertyName: 'data',
  underlineHeaders: true,
  headerAngle: 0,
  fillStyle: 'black',
  strokeStyle: 'black',
  lineWidth: 1
};

const setCanvasHeight = $window => {
  const canvas = document.querySelector(`#${PHYLOCANVAS_DIV}`);
  canvas.style.height = `${$window.innerHeight - 250}px`;
};

/**
 * Angular controller function for this scope.
 * @param {object} $window AngularJS window object
 * @param {object} $scope AngularJS $scope object for current dom
 * @param {object} PhylocanvasService angular service for server exchanges for newick data
 */
function controller($window, $scope, PhylocanvasService) {
  // Make the canvas fill the viewable window.
  setCanvasHeight($window);

  // Initialize phylocanvas.
  const tree = Phylocanvas
    .createTree(PHYLOCANVAS_DIV, {
      metadata: metadataFormat
    });

  /**
   * Update the tree leaves with new metadata
   * @param {object} metadata Map of leafs with their metadata
   */
  const updateMetadata = metadata => {
    console.log(metadata);
    for (const leaf of tree.leaves) {
      leaf.data = metadata[leaf.label];
    }
    if (tree.drawn) {
      tree.fitInPanel();
      tree.draw();
    }
  };

  // Set tree defaults
  tree.setTreeType('rectangular');
  tree.alignLabels = true;
  tree.on('beforeFirstDraw', () => {
    // Adding empty metadata to start.
    const empty = {};
    tree.leaves
      .forEach(leaf => {
        empty[leaf.label] = {};
      });
    updateMetadata(empty);
  });

  /**
   * Listen for changes to the metadata structure and update
   * the phylocanvas accordingly.
   */
  $scope.$on(METADATA.UPDATED, (event, args) => updateMetadata(args.metadata));

  /**
   * Kick everything off by getting the newick file and the
   * initial metadata.
   */
  PhylocanvasService.getNewickData(this.newickurl)
    .then(newick => tree.load(newick));
}

export const PhylocanvasComponent = {
  bindings: {
    newickurl: '@',
    template: '@'
  },
  templateUrl: 'phylocanvas.tmpl.html',
  controller
};
