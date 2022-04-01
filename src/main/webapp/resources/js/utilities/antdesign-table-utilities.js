/**
 * This file is for any dynamic settings required for an ant design table.
 */

/**
 * Set the table page size selector options
 */
export function getPageSizeOptions(totalEntries) {
  if (totalEntries > 10 && totalEntries <= 20) {
    return ["10", "20"];
  } else if (totalEntries <= 50) {
    return ["10", "20", "50"];
  } else {
    return ["10", "20", "50", "100"];
  }
}
