require('style!./../../../../../css/components/ngFileUpload.css');
/**
 * @file AngularJS component displaying the capability of uploading an excel file
 * (via dropzone).
 */
const metadataUploader = {
  templateUrl: 'upload.component.tmpl.html',
  controller($state, $stateParams, sampleMetadataService) {
    // Display any errors if they were sent.
    this.errors = $stateParams.errors;

    this.uploadFiles = function(files) {
      sampleMetadataService.uploadMetadata(files[0])
        .then(() => {
          $state.go('sampleId');
        });
    };
  }
};

export default metadataUploader;
