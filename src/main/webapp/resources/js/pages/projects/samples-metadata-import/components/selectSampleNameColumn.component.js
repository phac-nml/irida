const selectSampleNameColumnComponent = {
  templateUrl: 'sampleId.tmpl.html',
  controller(sampleMetadataService, $state) {
    sampleMetadataService
      .getProjectData()
      .then(data => {
        // If there are no headers than no file has been uploaded,
        // therefore we need to go to the upload page.
        if (!data.hasOwnProperty('headers')) {
          $state.go('upload');
        }

        // Check to see if an 'idColumn' has already been set.
        if (data.hasOwnProperty('idColumn')) {
          this.idColumn = data.idColumn;
        } else {
          this.idColumn = data.headers[0];
        }
        this.headers = data.headers;
      });

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
