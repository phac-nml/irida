import React from "react";
import { Card, Col, Row, Skeleton, Spin } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { useLaunchState } from "./launch-context";
import { LaunchPageHeader } from "./LaunchPageHeader";

/**
 * React component to render the pipeline launch page.
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchPage() {
  const { loading } = useLaunchState();

  const content = loading ? <Skeleton /> : <LaunchPageHeader />;

  return (
    <Row>
      <Col md={{ span: 12, offset: 5 }} style={{ marginTop: SPACE_LG }}>
        <Card>
          <Spin spinning={loading} delay={500}>
            {content}
          </Spin>
        </Card>
      </Col>
    </Row>
  );
}
