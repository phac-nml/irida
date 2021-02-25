import React from "react";
import { render } from "react-dom";
import { Col, Divider, Row, Space, Typography } from "antd";
import { ProcessingCoverage } from "./ProcessingCoverage";
import { ProcessingAutomatedPipelines } from "./ProcessingAutomatedPipelines";

/**
 * Base script for displaying project process page
 * @returns {JSX.Element}
 * @constructor
 */
const ProcessingLayout = () => {
  const [projectId] = React.useState(
    () => window.location.href.match(/projects\/(\d+)\/settings/i)[1]
  );

  const { canManage } = window.project;

  return (
    <Row>
      <Col lg={24} xl={16} xxl={12}>
        <Typography.Title level={2}>
          {i18n("Processing.title")}
        </Typography.Title>
        <Space style={{ width: `100%` }} direction="vertical">
          <ProcessingCoverage projectId={projectId} canManage={canManage} />
          <Divider />
          <ProcessingAutomatedPipelines
            projectId={projectId}
            canManage={canManage}
          />
        </Space>
      </Col>
    </Row>
  );
};

render(<ProcessingLayout />, document.querySelector("#process-root"));
