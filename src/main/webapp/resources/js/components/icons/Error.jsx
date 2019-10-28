/**
 * Component to render an Error icon
 */

import React from "react";
import { Icon } from "antd";
import { red6 } from "../../styles/colors";
import { SPACE_SM } from "../../styles/spacing";

/**
 * Stateless UI component for displaying an 'Error' icon
 *
 *
 * @returns {Element} - Returns an 'Error' icon component
 */

export function Error() {
  return (
    <Icon type="close-circle" style={{ marginRight: SPACE_SM, color: red6 }} />
  );
}
