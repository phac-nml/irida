import React from "react";
import { Card, Col, Row, Skeleton, Spin } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { useLaunch } from "./launch-context";
import { LaunchContent } from "./LaunchContent";

/**
 * React component to render the pipeline launch page.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchPage() {
  const [{ loading }] = useLaunch();

  const content = loading ? <Skeleton /> : <LaunchContent />;

  return (
    <Row>
      <Col
        xxl={{ span: 16, offset: 4 }}
        xl={{ span: 20, offset: 2 }}
        style={{ marginTop: SPACE_LG }}
      >
        <Spin spinning={loading} delay={500}>
          {content}
        </Spin>
      </Col>
    </Row>
  );
}
