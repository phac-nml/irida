import React from "react";
import { Alert } from "antd";

interface DefaultErrorBoundaryProps {
  message?: string;
  description?: string;
}

/**
 * Default error boundary for react-router-dom
 * @param message "message" attribute for Alert component
 * @param description "description" attribute for Alert component
 * @constructor
 */
function DefaultErrorBoundary({
  message = i18n("DefaultErrorBoundary.message"),
  description = i18n("DefaultErrorBoundary.description"),
}: DefaultErrorBoundaryProps): JSX.Element {
  return (
    <Alert message={message} description={description} type="error" showIcon />
  );
}

export default DefaultErrorBoundary;
