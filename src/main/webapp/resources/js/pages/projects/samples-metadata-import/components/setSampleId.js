const setSampleId = {
  templateUrl: "sampleId.tmpl.html",
  controller(sampleMetadataService, $state) {
    const headers = sampleMetadataService.getHeaders();
    if (headers === null) {
      $state.go("upload");
    }

    this.headers = headers;
    this.idColumn = this.headers[0];
  }
};

export default setSampleId;
