import uniqolor from "uniqolor";

/**
 * Create a unique color for any IRIDA thing (project, sample, etc.)
 * @param id identifier for the thing
 * @param label label for the thing
 * @returns {{background: *, text: *}}
 */
export function generateColourForItem({ id, label }) {
  let background = uniqolor(`${label}-${id}`, {
    lightness: 95,
    saturation: 95,
  });
  let colour = uniqolor(`${label}-${id}`, {
    lightness: 40,
    saturation: 95,
  });
  console.log(colour);
  return { background: background.color, text: colour.color };
}
