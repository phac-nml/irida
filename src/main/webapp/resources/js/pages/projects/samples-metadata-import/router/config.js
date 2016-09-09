import {STATE_URLS} from "../constants";
import uploadState from "./states/uploadState";
import sampleIdState from "./states/sampleIdState";
import resultTableState from "./states/resultTableState";

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state("upload", uploadState)
    .state("sampleId", sampleIdState)
    .state("results", resultTableState);

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise(STATE_URLS.upload);
};
