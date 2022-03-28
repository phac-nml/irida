/**
 * Format Sort Order from the Ant Design sorter object
 * @param {array | object} sorter Ant Design sorter object
 * @returns array of Sort Order objects
 */
export function formatSort(sorter) {
  const order = { ascend: "asc", descend: "desc" };
  const formatProperty = (property) => property.join(".");
  const fromSorter = (item) => ({
    property: formatProperty(item.field),
    direction: order[item.order],
  });

  if (Array.isArray(sorter)) {
    return sorter.map(fromSorter);
  } else if (typeof sorter.order === "undefined") {
    return undefined;
  }
  return [fromSorter(sorter)];
}

export const stringSorter = (property) => (a, b) =>
  a[property].localeCompare(b[property], window.TL.LANGUAGE_TAG, {
    sensitivity: "base",
  });
