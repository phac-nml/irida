/**
 * Controller for MetadataComponent
 * @param {object} MetadataService for server calls
 */
function controller(MetadataService) {
  this.checkedTerms = {};
  MetadataService.getMetadata(this.metadataurl)
    .then(terms => {
      this.terms = terms;
    });

  this.getUpdateMetadata = () => {
    const keyIndexes = Object.keys(this.checkedTerms)
      .filter(term => this.checkedTerms[term]);
    const keys = keyIndexes.map(index => this.terms[index]);
    MetadataService.getMetadataForKeys(keys);
  };
}

export const MetadataComponent = {
  bindings: {
    metadataurl: '@'
  },
  templateUrl: 'metadata-component.tmpl.html',
  controller
};
