import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

/**
 * Component to render an [antd Alert]{@link https://ant.design/components/alert/}
 * specifically for displaying information with a consistent icon.
 */
export function InfoAlert({ message, description, ...props }) {
  return (
    <Alert
      type="info"
      showIcon
      message={message}
      description={description}
      {...props}
    />
  );
}

InfoAlert.propTypes = {
  message: PropTypes.string.isRequired,
  description: PropTypes.string
};
