/**
 * @file Configuration file for ui.router.
 */
import uploadState from './states/upload.state';
import sampleNameState from './states/sampleName.state';
import resultsState from './states/results.state';
import resultsFoundState from './states/results.found.state';
import resultsMissingState from './states/results.missing.state';

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state(uploadState)
    .state(sampleNameState)
    .state(resultsState)
    .state(resultsFoundState)
    .state(resultsMissingState);

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise('/upload');
};
