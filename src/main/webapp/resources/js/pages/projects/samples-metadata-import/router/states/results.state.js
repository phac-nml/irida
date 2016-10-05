/**
 * @file ui.router state for setting the results table for uploading metadata to samples.
 */
const resultsState = {
  name: 'results',
  url: '/results',
  component: 'resultsTable',
  resolve: {
    data(sampleMetadataService) {
      return sampleMetadataService
        .getProjectData();
    }
  }
};

export default resultsState;
