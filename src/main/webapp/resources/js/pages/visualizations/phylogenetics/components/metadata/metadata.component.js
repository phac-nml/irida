import {METADATA} from './../../constants';
/**
 * Controller for MetadataComponent
 * @param {object} $rootScope angular root scope.
 * @param {object} MetadataService for server calls
 */
function controller($rootScope, MetadataService) {
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
    const columns = this.terms
      .filter(term => term.selected)
      .map(term => term.term);
    $rootScope.$broadcast(METADATA.UPDATED, {columns});
  };
}

export const MetadataComponent = {
  bindings: {
    metadataurl: '@'
  },
  templateUrl: 'metadata-component.tmpl.html',
  controller
};
