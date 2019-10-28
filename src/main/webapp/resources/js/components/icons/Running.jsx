/**
 * Component to render a Running icon
 */

import React from "react";
import { Icon } from "antd";
import { grey6 } from "../../styles/colors";
import { SPACE_SM } from "../../styles/spacing";

/**
 * Stateless UI component for displaying a 'Running' icon
 *
 *
 * @returns {Element} - Returns a 'Running' icon component
 */

export function Running() {
  return (
    <Icon type="loading" style={{ marginRight: SPACE_SM, color: grey6 }} />
  );
}
