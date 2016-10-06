/**
 * @file ui.router state for displaying rows that have matched with a sample.
 */
const resultsMissingState = {
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
};

export default resultsMissingState;
