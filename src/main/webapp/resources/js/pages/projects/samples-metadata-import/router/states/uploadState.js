/**
 * @file ui.router state for handling uploading an Excel file
 * for adding metadata to multiple samples.
 */
import {STATE_URLS} from "../../constants";

/**
 * Angular Controller for handling metadata file dropping
 * @param {object} $state ui.router state object.
 * @constructor
 */
function SampleMetadataUploaderController($state) {
  const vm = this;
  let headers = [];
  vm.onSuccess = (file, result) => {
    headers = result;
  };

  /**
   * Once the file is uploaded successfully, we need to
   * transition to selecting the sample id column.
   */
  vm.onComplete = () => {
    $state.go("sampleId", {headers: headers});
  };
}

const uploadState = {
  url: STATE_URLS.upload,
  templateUrl: "upload.tmpl.html",
  controllerAs: "uploaderCtrl",
  controller: ["$state", SampleMetadataUploaderController]
};

export default uploadState;
