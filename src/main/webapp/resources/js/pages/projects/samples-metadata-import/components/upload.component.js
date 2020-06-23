import "../../../../../css/components/ngFileUpload.css";

/**
 * @file AngularJS component displaying the capability of uploading an excel file
 * (via dropzone).
 */
const metadataUploader = {
  templateUrl: "upload.component.tmpl.html",
  controller: [
    "$state",
    "$stateParams",
    "sampleMetadataService",
    function ($state, $stateParams, sampleMetadataService) {
      // Display any errors if they were sent.
      this.errors = $stateParams.errors;

      this.uploadFiles = function (files) {
        sampleMetadataService.uploadMetadata(files[0]).then(
          () => {
            // If the response is 'success' go to the next stage
            // to select which column is the sample name
            $state.go("sampleId");
          },
          () => {
            // If the response is 'error' stay on this stage
            // display the file warning message.
            this.badFile = true;
          }
        );
      };
    },
  ],
};

export default metadataUploader;
