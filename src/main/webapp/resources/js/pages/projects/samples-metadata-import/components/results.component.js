const resultsComponent = {
  templateUrl: 'results.component.tmpl.html',
  bindings: {
    data: '='
  },
  controller(sampleMetadataService) {
    this.found = this.data.found.length;
    this.missing = this.data.missing.length;

    this.saveMetadata = () => {
      sampleMetadataService
        .saveMetadata()
        .then(errors => {
          const types = Object.keys(errors);
          if (types.length === 0) {
            console.log('No errors yet!');
          }
        });
    };
  }
};

export default resultsComponent;
