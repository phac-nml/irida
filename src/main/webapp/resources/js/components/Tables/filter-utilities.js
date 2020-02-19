/**
 * @File Filter utilities for the ant.design table filters.
 */
import uniqBy from "lodash/uniqBy";

/**
 * Create a unique list of objects formatted for the ant.design Table column filter
 * @param list of objects to be filtered.
 * @param attr attribute to filter by.
 * @returns {{text: *, value: *}[]}
 */
export function createListFilterByUniqueAttribute({ list, attr }) {
  // Filter to ensure that all the keys captured are not `undefined`
  const filtered = list.filter(p => p[attr]);

  // Get 1 of each object containing the attribute
  const unique = uniqBy(filtered, attr);

  // Convert it into the format for the ant.design column filter
  const converted = unique.map(item => ({
    text: item[attr],
    value: item[attr]
  }));

  // Sort new list alphabetically so users can quickly find what they are looking for.
  converted.sort((a, b) => ("" + a.text).localeCompare(b.text));

  // Add a generic element for filtering on items that do not have this attribute.
  converted.push({ text: "Unknown", value: "unknown" });

  return converted;
}
