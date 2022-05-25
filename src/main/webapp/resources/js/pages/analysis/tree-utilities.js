import uniqolor from "uniqolor";

const EMPTY_COLOUR = "#ffffff";

/**
 * Generate a colour for each unique value per metadata term
 * @param {object} metadata Map of sampleNames to Map of metadata terms to metadata values
 * @param {array} terms List of metadata terms
 */
export function generateColourMap(metadata, terms) {
  const colourMap = terms.reduce((prev, curr) => {
    return Object.assign(prev, { [curr]: { "": EMPTY_COLOUR } });
  }, {});

  Object.values(metadata).forEach((sampleMetadata) => {
    Object.keys(sampleMetadata).forEach((term) => {
      const value = sampleMetadata[term].value;
      if (!(value in colourMap[term])) {
        colourMap[term][value] = uniqolor(value)["color"];
      }
    });
  });

  return colourMap;
}

/**
 * Format the metadata into an object that can be consumed by Phylocanvas.
 *  { leaf-label: { templateMetadataField : { label, color} }}
 * @param {array} metadata list of metaterms for the samples.
 * @param {array} metadataFieldLabels list of metadata field labels
 * @return {object} Map of metadata with colours for Phylocanvas to consume.
 */
export function formatMetadata(metadata, terms, colourMap) {
  const sampleMetadataTemplate = () => {
    return terms.reduce((prev, curr) => {
      return Object.assign(prev, {
        [curr]: { label: "", colour: colourMap[curr][""] },
      });
    }, {});
  };

  const formatSampleMetadata = (sampleMetadata, colourMap) => {
    return Object.keys(sampleMetadata).reduce((prev, curr) => {
      return Object.assign(prev, {
        [curr]: {
          label: sampleMetadata[curr].value,
          colour: colourMap[curr][sampleMetadata[curr].value],
        },
      });
    }, {});
  };

  const formattedMetadata = Object.keys(metadata).reduce((prev, curr) => {
    return Object.assign(prev, {
      [curr]: {
        ...sampleMetadataTemplate(),
        ...formatSampleMetadata(metadata[curr], colourMap),
      },
    });
  }, {});

  return formattedMetadata;
}
