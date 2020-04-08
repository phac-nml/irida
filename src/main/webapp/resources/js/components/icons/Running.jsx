/**
 * Component to render a Running icon with
 * an optional message
 */

import React from "react";
import { grey6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";
import { IconLoading } from "./Icons";

/**
 * Stateless UI component for displaying a 'Running' icon
 *
 * @param {string} message - Message to display next to icon
 *
 * @returns {Element} - Returns a 'Running' icon component
 */
export function Running({ message }) {
  return (
    <span>
      <IconLoading style={{ marginRight: SPACE_XS, color: grey6 }} />
      {message}
    </span>
  );
}
