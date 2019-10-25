/**
 * Component to render a Warning alert with an icon
 */

import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

export function WarningAlert({ message, description, ...props }) {
  return (
    <Alert
      type="warning"
      showIcon
      message={message}
      description={description}
      {...props}
    />
  );
}

WarningAlert.propTypes = {
  message: PropTypes.string.isRequired,
  description: PropTypes.string
};
