/**
 * Component to render an Info alert with an icon
 */

import React from "react";
import { Alert, AlertProps } from "antd";

/**
 * Stateless UI component for displaying an [antd info Alert]{@link https://ant.design/components/alert/}
 *
 * @param message - Text to display in alert
 * @param description - Optional description
 * @param props - remainder of props passed
 */
export function InfoAlert({
  message,
  description,
  ...props
}: AlertProps): JSX.Element {
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
