import { MetadataColourMap, TreeProperties } from "../../types/phylocanvas";

/**
 * Export a SVG blob of the provided legend section identified by term
 * @param {string} term the legend section to download
 * @param {object} colourMap Map of metadata terms and values to colours for phylocanvas to consume
 * @param {object} treeProps Map of treeProps for phylocanvas
 * @returns
 */
export function exportLegendSVG(
  term: string,
  colourMap: MetadataColourMap,
  treeProps: TreeProperties
) {
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
    `<svg xmlns="http://www.w3.org/2000/svg" width="${width}" height="${height}">\n`
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

/**
 * Used to sort sections of the phylocanvas legend alphabetically
 * @param a
 * @param b
 * @return {number|number}
 */
export const sortLegendSection = (a: string, b: string) => {
  if (a === "") return 1;
  if (b === "") return -1;
  return a.toLocaleLowerCase() < b.toLocaleLowerCase() ? -1 : 1;
};
