
const templateUrl = 'metadata.button.tmpl';
const asideTemplateUrl = 'metadata.aside.tmpl';

const FIELDS = window.headersList.map((header, index) => {
  return ({text: header, index, selected: true});
});

export const MetadataComponent = {
  templateUrl,
  require: {
    parent: '^^linelist'
  },
  controller: class MetadataComponent {
    constructor($aside) {
      this.$aside = $aside;
    }

    showMetadataTemplator() {
      const vm = this;
      this.$aside.open({
        templateUrl: asideTemplateUrl,
        openedClass: 'metadata-open',
        controllerAs: '$ctrl',
        controller() {
          this.fields = Object.assign(FIELDS);
          this.toggleField = vm.parent.updateColumnVisibility;
        },
        placement: 'left',
        size: 'sm'
      });
    }
  }
};
