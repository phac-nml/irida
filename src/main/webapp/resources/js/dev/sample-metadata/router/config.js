import {STATE_URLS} from "./../constants";
import uploadState from "./states/uploadState";
import sampleIdState from "./states/sampleIdState";

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state("upload", uploadState)
    .state("sampleId", sampleIdState);

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise(STATE_URLS.upload);
};
