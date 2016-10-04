const selectSampleNameColumnComponent = {
  templateUrl: 'sampleId.tmpl.html',
  controller(sampleMetadataService, $state) {
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
        .setSampleIdColumn(this.idColumn)
        .then(() => {
          $state.go('results');
        });
    };

    this.selectNewFile = () => {
      sampleMetadataService.clearProject();
      $state.go('upload');
    };
  }
};

export default selectSampleNameColumnComponent;
