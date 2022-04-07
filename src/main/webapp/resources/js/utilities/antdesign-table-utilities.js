/**
 * This file is for any dynamic settings required for an ant design table.
 */

/**
 * Default page size for ant design tables
 */
export const defaultPageSize = 10;

export const defaultShowPageSizeChanger = true;

/**
 * Set the common pagination options for the ant design tables
 */
export function getPaginationOptions(totalEntries) {
  const config = {
    total: totalEntries,
    hideOnSinglePage: totalEntries <= defaultPageSize,
    showSizeChanger: defaultShowPageSizeChanger,
    defaultPageSize: defaultPageSize,
  };

  if (totalEntries <= 10) {
    return { ...config, pageSizeOptions: [10] };
  } else if (totalEntries <= 20) {
    return { ...config, pageSizeOptions: [10, 20] };
  } else if (totalEntries <= 50) {
    return {
      ...config,
      pageSizeOptions: [10, 20, 50],
    };
  } else {
    return {
      ...config,
      pageSizeOptions: [10, 20, 50, 100],
    };
  }
}
