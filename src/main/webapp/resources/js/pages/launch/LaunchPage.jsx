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
      <Col xl={{ span: 14, offset: 5 }} style={{ marginTop: SPACE_LG }}>
        <Card>
          <Spin spinning={loading} delay={500}>
            {content}
          </Spin>
        </Card>
      </Col>
    </Row>
  );
}
