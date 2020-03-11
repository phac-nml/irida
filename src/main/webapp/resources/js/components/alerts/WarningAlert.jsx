/**
 * Component to render a Warning alert with an icon
 */

import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

/**
 * Stateless UI component for displaying an [antd warning Alert]{@link https://ant.design/components/alert/}
 *
 * @param {string} message - Text to display in alert
 * @param {string} description - Optional description
 *
 * @returns {Element} - Returns an antd warning 'Alert' component
 */

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
  /*Text to display in alert*/
  message: PropTypes.string.isRequired,
  /*Optional description*/
  description: PropTypes.string
};
