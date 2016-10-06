/**
 * @file ui.router state for displaying rows that have matched with a sample.
 */
const resultsFoundState = {
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
};

export default resultsFoundState;
