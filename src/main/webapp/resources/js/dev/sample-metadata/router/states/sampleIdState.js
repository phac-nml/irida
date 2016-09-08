import {STATE_URLS} from "../../constants";

/**
 * AngularJS Controller for the Sample Metadata Sample Id Selection
 * @param {object} $stateParams ui.router state params object
 * @param {object} sampleMetadataService Service for handling metadata
 * @constructor
 */
function SampleMetadataSampleIdController($stateParams, sampleMetadataService) {
  const vm = this;

  vm.headers = $stateParams.headers;

  vm.complete = function() {
    sampleMetadataService.setSampleIdColumn(vm.selectedColumn);
  };
}

const sampleIdState = {
  url: STATE_URLS.sampleId,
  params: {headers: null},
  templateUrl: "sampleId.tmpl.html",
  controllerAs: "sampleIdCtrl",
  controller: ["$stateParams", "sampleMetadataService",
    SampleMetadataSampleIdController]
};

export default sampleIdState;
