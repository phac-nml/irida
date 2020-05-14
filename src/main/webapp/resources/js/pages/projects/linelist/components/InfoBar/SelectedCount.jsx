import React from "react";

import PropTypes from "prop-types";

/**
 * Formats and displays selected sample counts
 */
export function SelectedCount(props) {
  return (
    <span tour="tour-counts">
      {props.count === 0
        ? i18n("linelist.selected.none")
        : props.count === 1
        ? i18n("linelist.selected.one")
        : i18n("linelist.selected.multiple").replace("_COUNT_", props.count)}
    </span>
  );
}

SelectedCount.propTypes = {
  count: PropTypes.number
};
