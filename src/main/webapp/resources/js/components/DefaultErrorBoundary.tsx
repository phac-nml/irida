import React from "react";
import { Alert, Col, Row } from "antd";
import { useRouteError } from "react-router-dom";

/**
 * Default error boundary for react-router-dom
 * @constructor
 */
function DefaultErrorBoundary(): JSX.Element {
  const error = useRouteError();
  return (
    <Row>
      <Col md={{ span: 12, offset: 6 }} sm={{ span: 24 }}>
        <Alert
          message={i18n("DefaultErrorBoundary.message")}
          description={error}
          type="error"
          showIcon
        />
      </Col>
    </Row>
  );
}

export default DefaultErrorBoundary;
