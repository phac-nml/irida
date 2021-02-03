import React from "react";
import { render } from "react-dom";
import { Col, Divider, Row, Space, Typography } from "antd";
import { ProcessingCoverage } from "./ProcessingCoverage";
import { ProcessingPriorities } from "./ProcessingPriorities";
import { ProcessingAutomatedPipelines } from "./ProcessingAutomatedPipelines";

const ProcessingLayout = () => {
  const [projectId] = React.useState(
    () => window.location.href.match(/projects\/(\d+)\/settings/i)[1]
  );

  return (
    <Row>
      <Col lg={24} xl={16} xxl={12}>
        <Typography.Title level={2}>
          {i18n("Processing.title")}
        </Typography.Title>
        <Space style={{ width: `100%` }} direction="vertical">
          <ProcessingPriorities projectId={projectId} />
          <Divider />
          <ProcessingCoverage projectId={projectId} />
          <Divider />
          <ProcessingAutomatedPipelines projectId={projectId} />
        </Space>
      </Col>
    </Row>
  );
};

render(<ProcessingLayout />, document.querySelector("#process-root"));
