/**
 * @file ui.router state for setting the column header associated with the
 * sample identifier.
 */
const sampleIdState = {
  name: 'sampleId',
  url: '/sampleId',
  component: 'selectSampleNameColumnComponent',
  resolve: {
    data(sampleMetadataService) {
      return sampleMetadataService.getProjectData();
    }
  }
};

export default sampleIdState;
