import Phylocanvas from 'phylocanvas';
import exportSvgPlugin from 'phylocanvas-plugin-export-svg';

const PHYLOCANVAS_DIV = 'phylocanvas';

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

  const tree = Phylocanvas.createTree(PHYLOCANVAS_DIV);
  tree.setTreeType('rectangular');
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
