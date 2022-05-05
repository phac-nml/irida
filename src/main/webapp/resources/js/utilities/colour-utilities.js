import stc from "string-to-color";

/**
 * Create a unique color for any IRIDA thing (project, sample, etc.)
 * @param id identifier for the thing
 * @param label label for the thing
 * @returns {string}
 */
export function generateColourForItem({ id, label }) {
  return stc(`${label}-${id}`);
}
