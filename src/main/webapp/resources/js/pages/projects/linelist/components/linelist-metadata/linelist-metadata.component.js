const templateUrl = 'metadata.button.tmpl';
const asideTemplateUrl = 'metadata.aside.tmpl';

/**
 * Controller for MetadataComponent. Handles displaying toggles
 * for hiding and showing metadata columsn,
 * @param {object} MetadataService service to get the metadata fields available.
 * @param {object} $aside Reference to the angular-aside instance
 */
function controller(MetadataService, $aside) {
  this.showMetadataTemplator = () => {
    const vm = this;
    $aside.open({
      templateUrl: asideTemplateUrl,
      openedClass: 'metadata-open',
      controllerAs: '$ctrl',
      controller() {
        this.fields = Object.assign(vm.fields);
        this.toggleField = vm.parent.updateColumnVisibility;
      },
      placement: 'left',
      size: 'sm'
    });
  };

  this.$onInit = () => {
    this.fields = MetadataService
      .getMetadataFields()
      .map((header, index) => {
        return ({text: header, index, selected: true});
      });
  };
}

controller.$inject = ['MetadataService', '$aside'];

export const MetadataComponent = {
  templateUrl,
  require: {
    parent: '^^linelist'
  },
  controller
};
