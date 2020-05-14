/**
 * Component to render an Error alert with an icon
 */

import React from "react";
import PropTypes from "prop-types";
import { Alert } from "antd";

/**
 * Stateless UI component for displaying an [antd error Alert]{@link https://ant.design/components/alert/}
 *
 * @param {string} message - Text to display in alert
 * @param {string} description - Optional description
 *
 * @returns {Element} - Returns an antd error 'Alert' component
 */

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
  /*Text to display in alert*/
  message: PropTypes.string.isRequired,
  /*Optional description*/
  description: PropTypes.string
};
