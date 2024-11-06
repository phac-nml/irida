import React from "react";

/**
 * Formats and displays the filtered counts for the table.
 */
export function FilteredCounts(props) {
  return (
    // eslint-disable-next-line react/no-unknown-property
    <span tour="tour-filter-counts">
      {i18n(
        "linelist.infobar.filterCounts",
        props.filterCount,
        props.totalSamples
      )}
    </span>
  );
}
