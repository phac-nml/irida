/**
 * @file Configuration file for ui.router.
 */
import uploadState from "./states/uploadState";
import sampleIdState from "./states/sampleIdState";
// import resultTableState from "./states/resultTableState";

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state(uploadState)
    .state(sampleIdState);
    // .state(resultTableState);

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise("/upload");
};
