import Phylocanvas from 'phylocanvas';
import metadataPlugin from 'phylocanvas-plugin-metadata';
import exportSvgPlugin from 'phylocanvas-plugin-export-svg';
import {METADATA} from './../../constants';

const PHYLOCANVAS_DIV = 'phylocanvas';

Phylocanvas.plugin(metadataPlugin);
Phylocanvas.plugin(exportSvgPlugin);

const metadataFormat = {
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
};

const setCanvasHeight = $window => {
  const canvas = document.querySelector(`#${PHYLOCANVAS_DIV}`);
  canvas.style.height = `${$window.innerHeight - 200}px`;
};

/**
 * Angular controller function for this scope.
 * @param {object} $window AngularJS window object
 * @param {object} $scope AngularJS $scope object for current dom
 * @param {object} PhylocanvasService angular service for server exchanges for newick data
 * @param {object} MetadataService angular service for server exchanges for metadata data
 */
function controller($window, $scope, PhylocanvasService, MetadataService) {
  // Make the canvas fill the viewable window.
  setCanvasHeight($window);

  // Initialize phylocanvas.
  const tree = Phylocanvas
    .createTree(PHYLOCANVAS_DIV, {
      metadata: metadataFormat
    });

  /**
   * Update the tree leaves with new metadata
   */
  const updateMetadata = () => {
    console.log(this.metadata);
    let prev;
    tree.leaves.forEach(leaf => {
      const data = this.metadata[leaf.label];
      if (data) {
        leaf.data = data;
      } else {
        leaf.data = prev;
      }
      console.log(leaf.data);
      prev = Object.assign({}, data);
    });
    if (tree.drawn) {
      tree.draw();
    }
  };

  // Set tree defaults
  tree.setTreeType('rectangular');
  tree.alignLabels = true;
  tree.on('beforeFirstDraw', () => updateMetadata());

  this.export = () => {
    console.info(tree.exportSVG.getSerialisedSVG());
  };

  /**
   * Listen for changes to the metadata structure and update
   * the phylocanvas accordingly.
   */
  $scope.$on(METADATA.UPDATED, (event, args) => {
    this.metadata = args.metadata;
    if (tree.drawn) {
      updateMetadata();
    } else {
      // Load the tree only when the initial metadata is available.
      tree.load(this.newick);
    }
  });

  /**
   * Kick everything off by getting the newick file and the
   * initial metadata.
   */
  PhylocanvasService.getNewickData(this.newickurl)
    .then(data => {
      this.newick = data;
      MetadataService.getMetadata(this.metadataurl, this.template);
    });
}

export const PhylocanvasComponent = {
  bindings: {
    newickurl: '@',
    metadataurl: '@',
    template: '@'
  },
  templateUrl: 'phylocanvas.tmpl.html',
  controller
};
