/**
 * Component to render an Error icon with
 * an optional message
 */

import React from "react";
import { red6 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";
import { IconCloseCircle } from "./Icons";

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
      <IconCloseCircle style={{ color: red6, marginRight: SPACE_XS }} />
      {message}
    </span>
  );
}
