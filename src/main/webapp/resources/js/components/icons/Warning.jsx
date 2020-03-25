/**
 * Component to render a Warning icon with
 * an optional message
 */

import React from "react";
import { Icon } from "antd";
import { yellow6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";

/**
 * Stateless UI component for displaying a 'Warning' icon
 *
 * @param {string} message - Message to display next to icon
 *
 * @returns {Element} - Returns a 'Warning' icon component
 */

export function Warning({ message }) {
  return (
    <span>
      <Icon
        type="info-circle"
        style={{ marginRight: SPACE_XS, color: yellow6 }}
      />
      {message}
    </span>
  );
}
