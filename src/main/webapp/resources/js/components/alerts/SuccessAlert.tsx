/**
 * Component to render a Success alert with an icon
 */

import React from "react";
import { Alert, AlertProps } from "antd";

interface Props extends AlertProps {
  message: any,
  description?: string,
}

/**
 * Stateless UI component for displaying an [antd success Alert]{@link https://ant.design/components/alert/}
 *
 * @param {string} message - Text to display in alert
 * @param {string} description - Optional description
 *
 * @returns {Element} - Returns an antd success 'Alert' component
 */

export const SuccessAlert: React.FC<Props> = ({ message, description, ...props }) => {
  return (
    <Alert
      type="success"
      showIcon
      message={message}
      description={description}
      {...props}
    />
  );
};