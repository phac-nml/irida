/**
 * @file AngularJS component displaying the capability of uploading an excel file
 * (via dropzone).
 */
const metadataUploader = {
  templateUrl: 'upload.component.tmpl.html',
  controller($state, Upload) {
    this.uploadFiles = function(files) {
      Upload
        .upload({
          url: '/projects/4/sample-metadata/pm-4',
          data: {file: files[0]}
        })
        .then(response => {
          console.log(response);
          $state.go('sampleId');
        });
    };
  }
};

export default metadataUploader;
