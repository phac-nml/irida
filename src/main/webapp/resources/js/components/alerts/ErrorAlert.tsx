/**
 * Component to render an Error alert with an icon
 */

import React from "react";
import { Alert, AlertProps } from "antd";

export interface ErrorAlertProps {
  message: string;
  description?: string;
  props?: AlertProps;
}

/**
 * Stateless UI component for displaying an [antd error Alert]{@link https://ant.design/components/alert/}
 *
 * @param {string} message - Text to display in alert
 * @param {string} description - Optional description
 * @param {object} props - any other props that are passed
 * @returns {Element} - Returns an antd error 'Alert' component
 */
export function ErrorAlert({
  message,
  description,
  ...props
}: ErrorAlertProps): JSX.Element {
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
