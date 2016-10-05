const metadataUploader = {
  templateUrl: 'upload.tmpl.html',
  controller(sampleMetadataService, $state) {
    sampleMetadataService.getProjectData().then(data => {
      // Check to see if the idColumn has been set
      this.hasHeaders = data.hasOwnProperty('headers');
    });

    this.onError = () => {
      console.log('ERROR');
    };

    this.onComplete = () => {
      $state.go('sampleId');
    };
  }
};

export default metadataUploader;
