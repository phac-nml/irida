const displayColumnHeaders = {
  templateUrl: "sampleId.tmpl.html",
  controller(sampleMetadataService, $state, $window) {
    const details = sampleMetadataService.getProjectData();

    // If there are no headers than no file has been uploaded,
    // therefore we need to go to the upload page.
    if (!details.hasOwnProperty("headers")) {
      $state.go("upload");
    }

    this.headers = details.headers;
    this.idColumn = this.headers[0];

    this.displayMetadata = () => {
      sampleMetadataService
        .setSampleIdColumn($window.location.pathname, this.idColumn)
        .then(() => {
          $state.go("results");
        });
    };
  }
};

export default displayColumnHeaders;
