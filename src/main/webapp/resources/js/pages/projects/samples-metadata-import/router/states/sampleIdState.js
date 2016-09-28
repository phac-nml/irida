/**
 * @file ui.router state for setting the column header associated with the
 * sample identifier.
 */
import {STATE_URLS} from "../../constants";

/**
 * Controller for setting the sample id for the table.
 * @param {object} $state ui.router state object.
 * @param {object} $stateParams ui.router state parameters object.
 * @constructor
 */
function SetSampleIdController($state, $stateParams) {
  const vm = this;

  if ($stateParams.headers.length === 0) {
    $state.go('upload');
  }

  vm.headers = $stateParams.headers;
  vm.rows = $stateParams.rows;

  vm.setHeader = header => {
    console.log(header);
  };
}

const sampleIdState = {
  url: STATE_URLS.sampleId,
  params: {headers: [], rows: []},
  templateUrl: "sampleId.tmpl.html",
  controllerAs: "sampleIdCtrl",
  controller: ["$state", "$stateParams", SetSampleIdController]
};

export default sampleIdState;
