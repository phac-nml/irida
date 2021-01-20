import React from "react";
import { IconFlag } from "../../../components/icons/Icons";
import { blue6, grey5 } from "../../../styles/colors";

/**
 * React component to be used any time a PriorityFlag is used.
 * @param {boolean} change color of flag based on priority
 * @param {object} props any other props that should be added to the icon
 * @returns {*}
 */
export function PriorityFlag({ hasPriority, ...props }) {
  return <IconFlag style={{ color: hasPriority ? blue6 : grey5 }} {...props} />;
}
