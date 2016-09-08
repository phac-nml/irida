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
    console.log(result);
    headers = result;
  };

  vm.onComplete = () => {
    console.log("COMPLETE");
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
