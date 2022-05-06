import stc from "string-to-color";
import chroma from "chroma-js";

/**
 * Create a unique color for any IRIDA thing (project, sample, etc.)
 * @param id identifier for the thing
 * @param label label for the thing
 * @returns {{background: *, text: *}}
 */
export function generateColourForItem({ id, label }) {
  let colour = stc(`${label}-${id}`);
  const background = chroma(colour).alpha(0.2).hex();
  const text = chroma(colour).darken(2.6);
  return { background, text };
}