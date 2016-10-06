/**
 * @file ui.router state for setting the results table for uploading metadata to samples.
 */
const resultsState = {
  name: 'results',
  url: '/results',
  component: 'resultsComponent',
  resolve: {
    data(sampleMetadataService) {
      return sampleMetadataService
        .getProjectData();
    }
  }
};

export default resultsState;
