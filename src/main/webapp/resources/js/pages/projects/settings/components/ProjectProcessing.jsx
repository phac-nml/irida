import React from "react";
import { Col, Divider, Row, Space, Typography } from "antd";
import { ProcessingCoverage } from "./processing/ProcessingCoverage";
import { ProcessingAutomatedPipelines } from "./processing/ProcessingAutomatedPipelines";
import { useSelector } from "react-redux";

/**
 * Base script for displaying project process page
 * @returns {JSX.Element}
 * @constructor
 */
export default function ProjectProcessing({ projectId }) {
  const { canManage } = useSelector((state) => state.project);

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
}
