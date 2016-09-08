import {STATE_URLS} from "../../constants";

/**
 * AngularJS Controller for the Sample Metadata Sample Id Selection
 * @param {object} $stateParams ui.router state params object
 * @constructor
 */
function SampleMetadataSampleIdController($stateParams) {
  const vm = this;

  vm.headers = $stateParams.headers;
}

const sampleIdState = {
  url: STATE_URLS.sampleId,
  params: {headers: null},
  templateUrl: "sampleId.tmpl.html",
  controllerAs: "sampleIdCtrl",
  controller: ["$stateParams", SampleMetadataSampleIdController]
};

export default sampleIdState;
