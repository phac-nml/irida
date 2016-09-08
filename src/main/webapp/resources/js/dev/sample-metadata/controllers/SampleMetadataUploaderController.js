/**
 * Angular Controller for handling metadata file dropping
 * @param {object} $state ui.router state object.
 * @constructor
 */
export default function SampleMetadataUploaderController($state) {
  const vm = this;

  vm.onSuccess = (file, result) => {
    console.log("SUCCESS", result);
  };

  vm.onComplete = () => {
    console.log("COMPLETE");
    $state.go("sampleId");
  };
}
