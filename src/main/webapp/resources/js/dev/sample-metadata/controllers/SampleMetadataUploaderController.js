/**
 * Angular Controller for handling metadata file dropping
 * @constructor
 */
export default function SampleMetadataUploaderController() {
  const vm = this;

  vm.onSuccess = (file, result) => {
    console.log("SUCCESS", result);
  };

  vm.onComplete = () => {
    console.log("COMPLETE");
  };
}
