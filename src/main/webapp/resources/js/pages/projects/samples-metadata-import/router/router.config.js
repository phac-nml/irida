/**
 * @file Configuration file for ui.router.
 */
import uploadState from './states/upload.state';
import sampleNameState from './states/sampleName.state';
import resultsState from './states/results.state';

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state(uploadState)
    .state(sampleNameState)
    .state(resultsState);

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise('/upload');
};
