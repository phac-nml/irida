/**
 * Component to render an Error icon with
 * an optional message
 */

import React from "react";
import { Icon } from "antd";
import { red6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";

/**
 * Stateless UI component for displaying an 'Error' icon
 *
 * @param {string} message - Message to display next to icon
 *
 * @returns {Element} - Returns an 'Error' icon component
 */

export function Error({ message }) {
  return (
    <span>
      <Icon
        type="close-circle"
        style={{ marginRight: SPACE_XS, color: red6 }}
      />
      {message}
    </span>
  );
}
