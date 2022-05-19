import uniqolor from "uniqolor";

const EMPTY_COLOUR = "transparent";

/**
 * Format the metadata into an object that can be conssome by Phylocanvas.
 *  { leaf-label: { templateMetadataField : { label, color} }}
 * @param {array} metadata list of metaterms for the samples.
 * @param {array} metadataFieldLabels list of metadata field labels
 * @return {object} Map of metadata with colours for Phylocanvas to consume.
 */
 export function formatMetadata(metadata, metadataFieldLabels) {
  const result = {};
  const colourMap = {};
  const sampleNames = Object.keys(metadata);

  for (const sampleName of sampleNames) {
    const sampleMetadata = metadata[sampleName];
    result[sampleName] = {};
    for (const field of metadataFieldLabels) {
      const metadataLabel = sampleMetadata[field].value;

      if (metadataLabel && metadataLabel.length !== 0) {
        // Find out if the field has already been assigned a colour
        // If not, get it a new one.
        colourMap[field] = colourMap[field] || {};
        colourMap[field][metadataLabel] =
          colourMap[field][metadataLabel] || uniqolor(metadataLabel)["color"];
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

  return result;
};