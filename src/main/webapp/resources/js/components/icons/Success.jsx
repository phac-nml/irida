/**
 * Component to render a Success icon with
 * an optional message
 */

import React from "react";
import { Icon } from "antd";
import { green6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";

/**
 * Stateless UI component for displaying a 'Success' icon
 *
 * @param {string} message - Message to display next to icon
 *
 * @returns {Element} - Returns a 'Success' icon component
 */

export function Success({ message }) {
  return (
    <span>
      <Icon
        type="check-circle"
        style={{ marginRight: SPACE_XS, color: green6 }}
      />
      {message}
    </span>
  );
}
