/**
 * @file AngularJS component displaying the capability of uploading an excel file
 * (via dropzone).
 */
const metadataUploader = {
  templateUrl: 'upload.component.tmpl.html',
  controller($state) {
    this.onError = () => {
      console.log('ERROR');
    };

    this.onComplete = () => {
      $state.go('sampleId');
    };
  }
};

export default metadataUploader;
