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
  columns: [],
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

  this.$onInit = () => {
    /**
     * Kick everything off by getting the newick file and the
     * initial metadata.
     */
    PhylocanvasService.getNewickData(this.newickurl)
      .then(newick => {
        this.newick = newick;
      });
  };

  // Initialize phylocanvas.
  const tree = Phylocanvas
    .createTree(PHYLOCANVAS_DIV, {
      metadata: metadataFormat
    });

  // Set tree defaults
  tree.setTreeType('rectangular');
  tree.alignLabels = true;
  tree.on('beforeFirstDraw', () => {
    for (const leaf of tree.leaves) {
      leaf.data = this.metadata[leaf.label];
    }
  });

  /**
   * Listen for changes to the metadata structure and update
   * the phylocanvas accordingly.
   */
  $scope.$on(METADATA.UPDATED, (event, args) => {
    tree.metadata.columns = args.columns;
    tree.draw();
    tree.fitInPanel();
    tree.draw();
  });

  $scope.$on(METADATA.LOADED, (event, args) => {
    this.metadata = args.metadata;
    tree.load(this.newick);
  });
}

export const PhylocanvasComponent = {
  bindings: {
    newickurl: '@'
  },
  templateUrl: 'phylocanvas.tmpl.html',
  controller
};
