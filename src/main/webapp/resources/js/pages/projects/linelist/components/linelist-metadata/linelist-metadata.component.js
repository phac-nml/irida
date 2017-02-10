const templateUrl = 'metadata.button.tmpl';
const asideTemplateUrl = 'metadata.aside.tmpl';

/**
 * Controller for MetadataComponent. Handles displaying toggles
 * for hiding and showing metadata columns,
 * @param {object} $aside Reference to the angular-aside instance
 */
function controller($aside) {
  this.$onInit = () => {
    this.fields = this.headers
      .map((header, index) => {
        return ({text: header, index, selected: true});
      });
  };

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
}

controller.$inject = ['$aside'];

export const MetadataComponent = {
  templateUrl,
  require: {
    parent: '^^linelist'
  },
  bindings: {
    headers: '<'
  },
  controller
};
