/**
 * Component to render an Info alert with an icon
 */

import React from "react";
import { Alert, AlertProps } from "antd";

interface Props extends AlertProps {
  message: any,
  description?: string,
}

/**
 * Stateless UI component for displaying an [antd info Alert]{@link https://ant.design/components/alert/}
 *
 * @param {string} message - Text to display in alert
 * @param {string} description - Optional description
 * @param {object} props - remainder of props passed
 * @returns {Element} - Returns an antd info 'Alert' component
 */
export const InfoAlert: React.FC<Props> = ({ message, description, ...props }) => {
  return (
    <Alert
      type="info"
      showIcon
      message={message}
      description={description}
      {...props}
    />
  );
};