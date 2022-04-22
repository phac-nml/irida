import React from "react";

/**
 * Utility wrapper to make sure that the popover contents have a standardized width
 * @param props
 * @returns {*}
 * @constructor
 */
export function PopoverContents(props) {
  return <div style={{ maxWidth: "250px" }}>{props.contents}</div>;
}