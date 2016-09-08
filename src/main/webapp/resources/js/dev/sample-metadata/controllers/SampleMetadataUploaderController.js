/**
 * Angular Controller for handling metadata file dropping
 * @param {object} $state ui.router state object.
 * @constructor
 */
export default function SampleMetadataUploaderController($state) {
  const vm = this;
  let headers = [];
  vm.onSuccess = (file, result) => {
    headers = result.headers;
  };

  vm.onComplete = () => {
    console.log("COMPLETE");
    $state.go("sampleId", {headers: headers});
  };
}
