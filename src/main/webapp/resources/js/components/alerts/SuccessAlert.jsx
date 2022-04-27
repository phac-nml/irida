/**
 * Component to render a Success alert with an icon
 */

import React from "react";
import { Alert } from "antd";

/**
 * Stateless UI component for displaying an [antd success Alert]{@link https://ant.design/components/alert/}
 *
 * @param {string} message - Text to display in alert
 * @param {string} description - Optional description
 *
 * @returns {Element} - Returns an antd success 'Alert' component
 */

export function SuccessAlert({ message, description, ...props }) {
  return (
    <Alert
      type="success"
      showIcon
      message={message}
      description={description}
      {...props}
    />
  );
}