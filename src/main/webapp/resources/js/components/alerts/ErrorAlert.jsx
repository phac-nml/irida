/**
 * Component to render an Error alert with an icon
 */

import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

export function ErrorAlert({ message, description, ...props }) {
  return (
    <Alert
      type="error"
      showIcon
      message={message}
      description={description}
      {...props}
    />
  );
}

ErrorAlert.propTypes = {
  message: PropTypes.string.isRequired,
  description: PropTypes.string
};
