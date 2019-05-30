import React from "react";
import i18n from "i18n";
import PropTypes from "prop-types";

/**
 * Formats and displays the filtered counts for the table.
 */
export function FilteredCounts(props) {
  return (
    <span tour="tour-filter-counts">
      {i18n("linelist.infobar.filterCounts")
        .replace("[FILTER_COUNT]", props.filterCount)
        .replace("[TOTAL]", props.totalSamples)}
    </span>
  );
}

FilteredCounts.propTypes = {
  filterCount: PropTypes.number,
  totalSamples: PropTypes.number
};
