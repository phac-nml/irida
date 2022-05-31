/**
 * Component to render a Warning alert with an icon
 */

import React from "react";
import { Alert, AlertProps } from "antd";

interface Props extends AlertProps {
  message: any,
  description?: string,
}

/**
 * Stateless UI component for displaying an [antd warning Alert]{@link https://ant.design/components/alert/}
 *
 * @param {string} message - Text to display in alert
 * @param {string} description - Optional description
 *
 * @returns {Element} - Returns an antd warning 'Alert' component
 */

export const WarningAlert: React.FC<Props> = ({ message, description, ...props }) => {
  return (
    <Alert
      type="warning"
      showIcon
      message={message}
      description={description}
      {...props}
    />
  );
};