/**
 * Component to render a Running icon with
 * an optional message
 */

import React from "react";
import { Icon } from "antd";
import { grey6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";

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
      <Icon type="loading" style={{ marginRight: SPACE_XS, color: grey6 }} />
      {message}
    </span>
  );
}
