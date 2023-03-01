/**
 * Component to render an Error alert with an icon
 */

import React from "react";
import { Alert } from "antd";
import { AlertProps } from "antd/lib/alert";

/**
 * Stateless UI component for displaying an [antd error Alert]{@link https://ant.design/components/alert/}
 *
 * @returns {JSX.Element} - Returns an antd error 'Alert' component
 */
export function ErrorAlert({
  message,
  description,
  ...props
}: AlertProps): JSX.Element {
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
