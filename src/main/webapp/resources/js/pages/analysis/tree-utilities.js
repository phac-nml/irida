import { ConsoleSqlOutlined } from "@ant-design/icons";
import uniqolor from "uniqolor";

const EMPTY_COLOUR = "#ffffff";

/**
 * Generate a colour for each unique value per metadata term
 * @param {object} metadata Map of sampleNames to Map of metadata terms to metadata values
 * @param {array} terms List of metadata terms
 */
export function generateColourMap(metadata, terms) {
  const colourMap = terms.reduce((prev, curr) => {
    return Object.assign(prev, { [curr]: {} });
  }, {});

  Object.values(metadata).forEach((sampleMetadata) => {
    terms.forEach((term) => {
      const value = term in sampleMetadata ? sampleMetadata[term].value : "";
      if (!(value in colourMap[term])) {
        if (value === "") {
          colourMap[term][value] = EMPTY_COLOUR;
        } else {
          colourMap[term][value] = uniqolor(value)["color"];
        }
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

export function exportLegendSVG(term, colourMap, treeProps) {
  const svg = [];
  const padding = 16;
  const blockLabelPadding = 8;
  const { blockLength, fontSize, fontFamily } = treeProps;
  const numValues = Object.keys(colourMap[term]).length;
  const x = padding;
  let y = padding + fontSize;

  const title = i18n(
    "visualization.phylogenomics.sidebar.legend.colour-by",
    term
  );

  const blankLabel = i18n("visualization.phylogenomics.metadata.fields.blank");

  const titleLength = title.length;
  const valueLength = Object.keys(colourMap[term]).sort(
    (a, b) => b.length - a.length
  )[0].length;

  const width = Math.max(
    titleLength * fontSize + 2 * padding,
    valueLength * fontSize + blockLength + blockLabelPadding + 2 * padding
  );
  const height =
    fontSize +
    2 * padding +
    numValues * (Math.max(fontSize, blockLength) + padding) +
    padding;

  svg.push(
    `<svg viewBox="0 0 ${width} ${height}" xmlns="http://www.w3.org/2000/svg">\n`
  );

  svg.push(`<g font-family="${fontFamily}" font-size="${fontSize}px">\n`);

  svg.push(
    `<text x="${x}" y="${y}" font-weight="bold" text-anchor="start">${title}</text>\n`
  );

  svg.push(`</g>\n`);

  const sortedKeys = Object.keys(colourMap[term]).sort((a, b) => {
    if (a === "") return 1;
    if (b === "") return -1;
    return a < b ? -1 : 1;
  });

  for (const key of sortedKeys) {
    y = y + padding + blockLength;
    svg.push(`<g font-family="${fontFamily}" font-size="${fontSize}px">\n`);

    const hex = colourMap[term][key];
    const red = parseInt(hex[1] + hex[2], 16);
    const green = parseInt(hex[3] + hex[4], 16);
    const blue = parseInt(hex[5] + hex[6], 16);

    svg.push(
      `<rect x="${x}" y="${
        y - blockLength
      }" width="${blockLength}" height="${blockLength}" fill="rgb(${red},${green},${blue})" />\n`
    );

    const label = key === "" ? blankLabel : key;

    svg.push(
      `<text x="${
        x + blockLength + padding
      }" y="${y}" text-anchor="start">${label}</text>\n`
    );

    svg.push(`</g>\n`);
  }

  svg.push("</svg>\n");

  return new Blob(svg, { type: "image/svg+xml" });
}
