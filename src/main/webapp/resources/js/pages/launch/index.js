import React from "react";
import { render } from "react-dom";
import { Card, Col, Row, Skeleton, Spin, Typography } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { LaunchProvider, useLaunchState } from "./launch-context";

function LaunchLayout() {
  const { loading, pipeline } = useLaunchState();

  const content = loading ? (
    <Skeleton />
  ) : (
    <>
      <Typography.Title>{pipeline.name}</Typography.Title>
      <Typography.Paragraph>{pipeline.description}</Typography.Paragraph>
    </>
  );

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

render(
  <LaunchProvider>
    <LaunchLayout />
  </LaunchProvider>,
  document.querySelector("#root")
);
