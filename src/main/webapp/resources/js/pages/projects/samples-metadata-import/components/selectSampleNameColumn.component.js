/**
 * @file AngularJS component for displaying a list of table headers.
 * The user can select the header that corresponds to the sample name.
 */
const selectSampleNameColumnComponent = {
  templateUrl: "sampleId.tmpl.html",
  bindings: {
    data: "="
  },
  controller: [
    "sampleMetadataService",
    "$state",
    function(sampleMetadataService, $state) {
      this.$onInit = () => {
        // If there are no headers or rows than no file has been uploaded,
        // therefore we need to go to the upload page.
        if (this.data.headers === null || this.data.rows === null) {
          $state.go("upload");

          // Check to make sure there are no duplicate headers
          const foundHeaders = {};
          this.data.headers.forEach(header => {
            if (foundHeaders[header]) {
              $state.go("upload", { errors: header });
            }
            foundHeaders[header] = true;
          });
        }

        // Check to see if an 'idColumn' has already been set.
        if (this.data.sampleNameColumn === null) {
          this.idColumn = this.data.headers[0];
        } else {
          this.idColumn = this.data.sampleNameColumn;
        }
        this.headers = this.data.headers;
      };

      /**
       * The selected header will be used as the sample name.
       * Once the samples are found on the server, the user will
       * be displayed the results.
       */
      this.displayMetadata = () => {
        sampleMetadataService.setSampleIdColumn(this.idColumn).then(() => {
          $state.go("results.found");
        });
      };

      /**
       * Redirect the user back to the upload page and remove the
       * previously uploaded file.
       */
      this.selectNewFile = () => {
        sampleMetadataService.clearProject();
        $state.go("upload");
      };
    }
  ]
};

export default selectSampleNameColumnComponent;
