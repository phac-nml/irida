/**
 * @file Configuration file for ui.router.
 */
/**
 * Make sure that all rows have the appropriate keys.
 * @param {array} headers rows header
 * @param {array} rows in table
 * @return {array} of rows with proper values
 */
function formatResults(headers, rows) {
  const _rows = Object.assign(rows);
  for (const row of _rows) {
    for (const header of headers) {
      if (!row.hasOwnProperty(header)) {
        row[header] = "";
      }
    }
  }
  return _rows;
}

/**
 *  Configuration for ui.router on the Sample Metadata Import Page
 * @param {object} $stateProvider ui.router stateProvider
 * @param {object} $urlRouterProvider ui.router urlRouterProvider
 */
export const states = ($stateProvider, $urlRouterProvider) => {
  $stateProvider
    .state({
      name: "upload",
      url: "/upload",
      component: "metadataUploader",
      params: {
        errors: null
      }
    })
    .state({
      name: "sampleId",
      url: "/sampleId",
      component: "selectSampleNameColumnComponent",
      resolve: {
        data: [
          "sampleMetadataService",
          function(sampleMetadataService) {
            return sampleMetadataService.getProjectData();
          }
        ]
      }
    })
    .state({
      name: "results",
      url: "/results",
      component: "resultsComponent",
      resolve: {
        data: [
          "sampleMetadataService",
          function(sampleMetadataService) {
            return sampleMetadataService.getProjectData();
          }
        ]
      }
    })
    .state({
      name: "results.found",
      url: "/found",
      component: "resultsFoundComponent",
      resolve: {
        rows: [
          "data",
          function(data) {
            return formatResults(data.headers, data.found);
          }
        ],
        headers: [
          "data",
          function(data) {
            return data.headers;
          }
        ]
      }
    })
    .state({
      name: "results.missing",
      url: "/missing",
      component: "resultsMissingComponent",
      resolve: {
        rows: [
          "data",
          function(data) {
            return formatResults(data.headers, data.missing);
          }
        ],
        headers: [
          "data",
          function(data) {
            return data.headers;
          }
        ]
      }
    });

  // Set the initial view as the upload view
  $urlRouterProvider.otherwise("/upload");
};
