const displayColumnHeaders = {
  templateUrl: 'sampleId.tmpl.html',
  controller(sampleMetadataService, $state, $window) {
    const details = sampleMetadataService.getProjectData();

    // If there are no headers than no file has been uploaded,
    // therefore we need to go to the upload page.
    if (!details.hasOwnProperty('headers')) {
      $state.go('upload');
    }

    // Check to see if an 'idColumn' has already been set.
    if (details.hasOwnProperty('idColumn')) {
      this.idColumn = details.idColumn;
    } else {
      this.idColumn = details.headers[0];
    }
    this.headers = details.headers;

    this.displayMetadata = () => {
      sampleMetadataService
        .setSampleIdColumn($window.location.pathname, this.idColumn)
        .then(() => {
          $state.go('results');
        });
    };
  }
};

export default displayColumnHeaders;
