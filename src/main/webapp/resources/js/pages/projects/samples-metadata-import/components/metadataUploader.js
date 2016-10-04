const metadataUploader = {
  templateUrl: 'upload.tmpl.html',
  controller(sampleMetadataService, $state) {
    const details = sampleMetadataService.getProjectData();

    // Check to see if the idColumn has been set
    if (details.hasOwnProperty('idColumn')) {
      console.log('The id column has recently been set');
    }

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
