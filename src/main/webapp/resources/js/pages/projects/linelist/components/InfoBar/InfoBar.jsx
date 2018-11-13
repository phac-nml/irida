import React from "react";
import PropTypes from "prop-types";
import { FilteredCounts } from "./FilteredCounts";
import { SelectedCount } from "./SelectedCount";

/**
 * Displays selected counts and filtered counts at the bottom of the table.
 */
export function InfoBar(props) {
  return (
    <div className="ag-grid-info-panel">
      <SelectedCount count={props.selectedCount} />
      <FilteredCounts
        filterCount={props.filterCount}
        totalSamples={props.totalSamples}
      />
    </div>
  );
}

InfoBar.propTypes = {
  selectedCount: PropTypes.number,
  filterCount: PropTypes.number.isRequired,
  totalSamples: PropTypes.number.isRequired
};
