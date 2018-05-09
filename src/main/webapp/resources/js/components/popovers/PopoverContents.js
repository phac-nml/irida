import React from "react";
import PropTypes from "prop-types";

/**
 * Utility wrapper to make sure that the popover contents have a standardized width
 * @param props
 * @returns {*}
 * @constructor
 */
export function PopoverContents(props) {
  return <div style={{ maxWidth: "250px" }}>{props.contents}</div>;
}

PopoverContents.propTypes = {
  contents: PropTypes.object.isRequired
};
