import React from "react";
import { Col, Row, Skeleton, Spin } from "antd";
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
        xs={{ span: 22, offset: 1 }}
        xl={{ span: 20, offset: 2 }}
        xxl={{ span: 10, offset: 7 }}
        style={{ marginTop: SPACE_LG }}
      >
        <Spin spinning={loading} delay={500}>
          {content}
        </Spin>
      </Col>
    </Row>
  );
}
