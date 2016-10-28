/**
 * Angular controller for the TemplateSelectorComponent
 * @param {object} MetadataService angular service for server exchanges for metadata data
 */
function controller(MetadataService) {
  this.templateChanged = () => {
    MetadataService.getMetadata(this.url, this.template);
  };
}

export const TemplateSelectorComponent = {
  bindings: {
    url: '@',
    template: '@'
  },
  templateUrl: 'templateSelector.tmpl.html',
  controller
};
