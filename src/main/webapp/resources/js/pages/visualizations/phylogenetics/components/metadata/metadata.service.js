import chroma from "chroma-js";
import { METADATA } from "./../../constants";

const EMPTY_COLOUR = "rgba(0,0,0,0)";

/**
 * Format the metadata into an object that can be conssome by Phylocanvas.
 *  { leaf-label: { templateMetadataField : { label, color} }}
 * @param {array} metadata list of metaterms for the samples.
 * @param {array} metadataFieldLabels list of metadata field labels
 * @return {object} Map of metadata with colours for Phylocanvas to consume.
 */
const formatMetadata = (metadata, metadataFieldLabels) => {
  const result = {};
  const colourMap = {};
  const sampleNames = Object.keys(metadata);

  for (const sampleName of sampleNames) {
    const sampleMetadata = metadata[sampleName];
    result[sampleName] = {};
    for (const field of metadataFieldLabels) {
      const metadataLabel = sampleMetadata[field].value;

      if (metadataLabel) {
        // Find out if the field has already been assigned a colour
        // If not, get it a new one.
        colourMap[field] = colourMap[field] || {};
        colourMap[field][metadataLabel] =
          colourMap[field][metadataLabel] || chroma.random().css();
        result[sampleName][field] = {
          label: metadataLabel,
          colour: colourMap[field][metadataLabel]
        };
      } else {
        // If the label is empty, then do not give it a colour.
        result[sampleName][field] = {
          label: metadataLabel,
          colour: EMPTY_COLOUR
        };
      }
    }
  }

  // Add the reference blanks, without this, Phylocanvas will througha fit!
  const reference = {};
  metadataFieldLabels.forEach(term => {
    reference[term] = { label: "", colour: EMPTY_COLOUR };
  });
  result.reference = reference;

  return result;
};

export class MetadataService {
  constructor($http, $window, $rootScope) {
    this.$rootScope = $rootScope;

    // Initialize the metadata
    $http.get($window.PAGE.urls.metadata).then(
      response => {
        // Keep a reference to the full list of terms
        const { terms, metadata } = response.data;

        if (Object.keys(metadata).length && terms.length) {
          // Set everything up.
          this.terms = terms;
          this.metadata = formatMetadata(metadata, terms);
          $rootScope.$broadcast(METADATA.LOADED, {
            metadata: this.metadata,
            terms
          });
        } else {
          this.$rootScope.$broadcast(METADATA.EMPTY);
        }
      },
      () => {
        this.$rootScope.$broadcast(METADATA.ERROR);
      }
    );
  }

  /**
   * Find the union between the terms defined in a template, and the terms available in
   * the metadata returned for the samples.
   * @param {array} templateTerms List of metadata_template_metadata_field associated with the
   *                      currently selected template.
   * @return {array} list of metadata terms to display in Phylocanvas
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

MetadataService.$inject = ["$http", "$window", "$rootScope"];
