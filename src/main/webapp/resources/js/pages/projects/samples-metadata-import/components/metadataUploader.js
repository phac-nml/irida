const metadataUploader = {
  templateUrl: "upload.tmpl.html",
  controller(sampleMetadataService, $state) {
    this.onSuccess = (file, result) => {
      sampleMetadataService.storeHeaders(result.headers);
    };

    this.onError = () => {
      console.log("ERROR");
    };

    this.onComplete = () => {
      $state.go("sampleId");
    };
  }
};

export default metadataUploader;
