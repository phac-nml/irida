/**
 * Component to render a Success alert with an icon
 */

import React from "react";
import { Alert, AlertProps } from "antd";

/**
 * Stateless UI component for displaying an [antd success Alert]{@link https://ant.design/components/alert/}
 *
 * @param message - Text to display in alert
 * @param description - Optional description
 * @param props - remainder of props passed
 */
export function SuccessAlert({
  message,
  description,
  ...props
}: AlertProps): JSX.Element {
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
