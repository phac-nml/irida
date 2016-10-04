const metadataUploader = {
  templateUrl: 'upload.tmpl.html',
  controller(sampleMetadataService, $state) {
    const details = sampleMetadataService.getProjectData();

    // Check to see if the idColumn has been set
    this.hasHeaders = details.hasOwnProperty('idColumn');

    this.onSuccess = (file, result) => {
      sampleMetadataService.storeProjectData({headers: result.headers});
    };

    this.onError = () => {
      console.log('ERROR');
    };

    this.onComplete = () => {
      $state.go('sampleId');
    };
  }
};

export default metadataUploader;
