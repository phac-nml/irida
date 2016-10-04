/**
 * @file Configuration file for ui.router.
 */
import uploadState from './states/upload.state';
import sampleNameState from './states/sampleName.state';
import resultTableState from './states/resultTable.state';

export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state(uploadState)
    .state(sampleNameState)
    .state(resultTableState);

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise('/upload');
};
