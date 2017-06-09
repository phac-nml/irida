/**
 * @file Configuration file for ui.router.
 */

/**
 *  Configuration for ui.router on the Sample Metadata Import Page
 * @param {object} $stateProvider ui.router stateProvider
 * @param {object} $urlRouterProvider ui.router urlRouterProvider
 */
export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state({
      name: 'upload',
      url: '/upload',
      component: 'metadataUploader',
      params: {
        errors: null
      }
    })
    .state({
      name: 'sampleId',
      url: '/sampleId',
      component: 'selectSampleNameColumnComponent',
      resolve: {
        data(sampleMetadataService) {
          return sampleMetadataService.getProjectData();
        }
      }
    })
    .state({
      name: 'results',
      url: '/results',
      component: 'resultsComponent',
      resolve: {
        data(sampleMetadataService) {
          return sampleMetadataService
            .getProjectData();
        }
      }
    })
    .state({
      name: 'results.found',
      url: '/found',
      component: 'resultsFoundComponent',
      resolve: {
        rows(data) {
          return data.found;
        },
        headers(data) {
          return data.headers;
        }
      }
    })
    .state({
      name: 'results.missing',
      url: '/missing',
      component: 'resultsMissingComponent',
      resolve: {
        rows(data) {
          return data.missing;
        },
        headers(data) {
          return data.headers;
        }
      }
    });

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise('/upload');
};
