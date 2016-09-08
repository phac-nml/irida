const bindToController = {
  url: "@"
};

/**
 * Controller for setting the sample id.
 * @param {object} $state ui.router state object.
 * @param {object} $stateParams ui.router state parameter object
 * @param {object} sampleMetadataService service to handle server calls
 * for metadata
 * @constructor
 */
function SetSampleIdController($state, $stateParams, sampleMetadataService) {
  const vm = this;
  if ($stateParams.headers.length === 0) {
    $state.go("upload");
  }

  vm.headers = $stateParams.headers;
  vm.selectedColumn = vm.headers[0];

  vm.complete = () => {
    sampleMetadataService.setSampleIdColumn(vm.url, vm.selectedColumn)
      .then(result => {
        console.log(result);
      });
  };
}

const setSampleId = () => {
  return {
    bindToController,
    restrict: "A",
    controllerAs: "setCtrl",
    controller: ["$state", "$stateParams", "sampleMetadataService",
      SetSampleIdController]
  };
};

export default setSampleId;
