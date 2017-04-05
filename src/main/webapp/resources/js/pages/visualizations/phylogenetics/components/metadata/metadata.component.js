class MetadataController {
  constructor() {
    console.log(this);
  }
}

export const MetadataComponent = {
  bindings: {
    terms: '<',
    onSelectionChange: '&'
  },
  templateUrl: 'metadata-component.tmpl.html',
  controller: MetadataController
};
