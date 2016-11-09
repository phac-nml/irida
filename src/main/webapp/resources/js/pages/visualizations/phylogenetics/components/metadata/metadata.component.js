import {METADATA} from './../../constants';
/**
 * Controller for MetadataComponent
 * @param {object} $rootScope angular root scope.
 * @param {object} $scope angular local dom scope.
 * @param {object} MetadataService for server calls
 */
function controller($rootScope, $scope, MetadataService) {
  // Get the initial metadata and terms.
  MetadataService.getMetadata(this.metadataurl)
    .then(results => {
      this.terms = results.terms.map(term => {
        return ({
          term,
          selected: true
        });
      });
      $rootScope.$broadcast(METADATA.LOADED, {metadata: results.metadata});
    });

  /**
   * Handler for when a checkbox's value changes.
   */
  this.getUpdateMetadata = () => {
    const keys = this.terms
      .filter(term => term.selected)
      .map(term => term.term);
    MetadataService.getMetadataForKeys(keys);
  };

  // Listen for template selection
  $scope.$on(METADATA.TEMPLATE, (ev, args) => {
    if (args === METADATA.ALL_FIELDS) {
      this.terms.forEach(term => {
        term.selected = true;
      });
    } else {
      const fields = new Set(args.fields);
      console.log(fields);
      this.terms.forEach(term => {
        term.selected = fields.has(term.term);
      });
    }
    this.getUpdateMetadata();
  });
}

export const MetadataComponent = {
  bindings: {
    metadataurl: '@'
  },
  templateUrl: 'metadata-component.tmpl.html',
  controller
};
