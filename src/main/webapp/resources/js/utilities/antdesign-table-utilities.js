/**
 * This file is for any dynamic settings required for an ant design table.
 */

/**
 * Default page size for ant design tables
 */
export const defaultPageSize = 10;

export const defaultShowPageSizeChanger = true;

/**
 * Set the common pagination options for the tables
 */
export function getPaginationOptions(totalEntries) {
  if (totalEntries <= 10) {
    return {
      pageSizeOptions: ["10"],
      hideOnSinglePage: true,
      showSizeChanger: false,
      pageSize: defaultPageSize,
    };
  } else if (totalEntries <= 20) {
    return {
      pageSizeOptions: ["10", "20"],
      hideOnSinglePage: totalEntries <= defaultPageSize,
      showSizeChanger: defaultShowPageSizeChanger,
      pageSize: defaultPageSize,
    };
  } else if (totalEntries <= 50) {
    return {
      pageSizeOptions: ["10", "20", "50"],
      hideOnSinglePage: totalEntries <= defaultPageSize,
      showSizeChanger: defaultShowPageSizeChanger,
      pageSize: defaultPageSize,
    };
  } else {
    return {
      pageSizeOptions: ["10", "20", "50", "100"],
      hideOnSinglePage: totalEntries <= defaultPageSize,
      showSizeChanger: defaultShowPageSizeChanger,
      pageSize: defaultPageSize,
    };
  }
}
