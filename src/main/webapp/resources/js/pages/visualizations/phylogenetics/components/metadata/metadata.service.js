import {getRandomColour} from './../../../../../utilities/colour.utilities';
import {METADATA} from './../../constants';

const EMPTY_COLOUR = 'rgba(0,0,0,0)';

const formatMetadata = (metadata, terms) => {
  const result = {};
  const colourMap = {};
  const ids = Object.keys(metadata);

  ids.forEach(id => {
    const data = metadata[id];
    result[id] = {};
    terms.forEach(term => {
      const label = data[term].value;

      if (label === '') {
        result[id][term] = {label, colour: EMPTY_COLOUR};
      } else {
        colourMap[term] = colourMap[term] || {};
        colourMap[term][label] =
          colourMap[term][label] || getRandomColour();
        result[id][term] = {label, colour: colourMap[term][label]};
      }
    });
  });

  // Add the reference blanks
  const reference = {};
  terms.forEach(term => {
    reference[term] = {label: '', colour: EMPTY_COLOUR};
  });
  result.reference = reference;

  return result;
};

export class MetadataService {
  constructor($http, $window, $rootScope) {
    this.$rootScope = $rootScope;

    // Initialize the metadata
    $http.get($window.PAGE.urls.metadata)
      .then(response => {
        // Keep a reference to the full list of terms
        const {terms, metadata} = response.data;

        if (Object.keys(metadata).length && terms.length) {
          // Set everything up.
          this.terms = terms;
          this.metadata = formatMetadata(metadata, terms);
          $rootScope.$broadcast(
            METADATA.LOADED, {metadata: this.metadata, terms}
          );
        } else {
          this.$rootScope.$broadcast(METADATA.EMPTY);
        }
      }, () => {
        this.$rootScope.$broadcast(METADATA.ERROR);
      });
  }

  getSortedAndFilteredColumns(terms) {
    if (terms) {
      return terms.filter(term => {
        return this.terms.includes(term);
      });
    }
    return [];
  }
}

MetadataService.$inject = [
  '$http',
  '$window',
  '$rootScope'
];

