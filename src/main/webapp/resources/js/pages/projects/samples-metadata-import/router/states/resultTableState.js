/**
 * @file ui.router state for displaying the result of uploading sample metadata.
 */
import {STATE_NAME} from "../../constants";

/**
 * Controller for displaying the result of uploading metadata.
 * @param {object} $state ui.router state object.
 * @param {object} $stateParams ui.router state parameter object
 * @constructor
 */
function ResultsController($state, $stateParams) {
  const vm = this;
  if ($stateParams.headers.length === 0) {
    $state.go("upload");
  }

  vm.headers = $stateParams.headers;
  vm.table = $stateParams.table;
  vm.notFound = $stateParams.notFound;
}

const resultTableState = {
  name: "result",
  url: "/results",
  params: {
    headers: [],
    notFound: [],
    table: []
  },
  templateUrl: "result.tmpl.html",
  controllerAs: "resultCtrl",
  controller: ["$state", "$stateParams", ResultsController]
};

export default resultTableState;
