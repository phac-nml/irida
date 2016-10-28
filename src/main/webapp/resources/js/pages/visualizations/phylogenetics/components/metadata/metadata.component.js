/**
 * Controller for metadata component
 * @param {object} MetadataService service for handling metadata
 */
function controller(MetadataService) {
  this.getUpdatedMetadataStructure = () => {
    MetadataService.formatMetadata();
  };
}

export const MetadataComponent = {
  templateUrl: 'metadata.tmpl.html',
  controller
};
