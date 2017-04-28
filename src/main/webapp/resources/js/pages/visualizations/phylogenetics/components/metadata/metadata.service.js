import {getRandomColour} from './../../../../../utilities/colour.utilities';
import {METADATA} from './../../constants';

const EMPTY_COLOUR = 'rgba(0,0,0,0)';

/**
 * Format the metadata into an object that can be conssome by Phylocanvas.
 *  { leaf-label: { templateMetadataField : { label, color} }}
 * @param {array} metadata list of metaterms for the samples.
 * @param {array} metadataFieldLabels list of metadata field labels
 */
const formatMetadata = (metadata, metadataFieldLabels) => {
  const result = {};
  const colourMap = {};
  const sampleNames = Object.keys(metadata);

  sampleNames.forEach(name => {
    const data = metadata[name];
    result[name] = {};
    metadataFieldLabels.forEach(field => {
      const label = data[field].value;

      if (label === '') {
        // If the label is empty, then do not give it a colour.
        result[label][field] = { label, colour: EMPTY_COLOUR };
      } else {
        // Find out if the field has already been assigned a colour
        // If not, get it a new one.
        colourMap[field] = colourMap[field] || {};
        colourMap[field][label] =
          colourMap[field][label] || getRandomColour();
        result[label][field] = { label, colour: colourMap[field][label] };
      }
    });
  });

  // Add the reference blanks, without this, Phylocanvas will througha fit!
  const reference = {};
  metadataFieldLabels.forEach(term => {
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

  /**
   * Find the union between the terms defined in a template, and the terms available in
   * the metadata returned for the samples.
   * @param {array} terms List of metadata_template_metadata_field associated with the
   *                      currently selected template.
   */
  getSortedAndFilteredColumns(templateTerms) {
    if (templateTerms) {
      return templateTerms.filter(term => {
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

