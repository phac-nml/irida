import React from "react";
import PropTypes from "prop-types";

/**
 * Formats and displays selected sample counts
 */
export function SelectedCount(props) {
  return (
    <span tour="tour-counts">
      {props.count === 0
        ? __("linelist.selected.none")
        : props.count === 1
        ? __("linelist.selected.one")
        : __("linelist.selected.multiple.replace")("_COUNT_", props.count)}
    </span>
  );
}

SelectedCount.propTypes = {
  count: PropTypes.number
};
