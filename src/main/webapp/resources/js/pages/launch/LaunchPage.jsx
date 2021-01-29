import React from "react";
import { Col, Layout, Row, Skeleton, Spin } from "antd";
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
    <Layout style={{ height: `100%`, width: `100%` }}>
      <Row>
        <Col
          xs={{ span: 22, offset: 1 }}
          xl={{ span: 20, offset: 2 }}
          xxl={{ span: 10, offset: 7 }}
        >
          <Spin spinning={loading} delay={500}>
            {content}
          </Spin>
        </Col>
      </Row>
    </Layout>
  );
}
