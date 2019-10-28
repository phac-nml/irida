/**
 * Component to render a Success icon
 */

import React from "react";
import { Icon } from "antd";
import { red6 } from "../../styles/colors";
import { SPACE_SM } from "../../styles/spacing";

/**
 * Stateless UI component for displaying a 'Success' icon
 *
 *
 * @returns {Element} - Returns a 'Success' icon component
 */

export function Success() {
  return (
    <Icon type="close-circle" style={{ marginRight: SPACE_SM, color: red6 }} />
  );
}
