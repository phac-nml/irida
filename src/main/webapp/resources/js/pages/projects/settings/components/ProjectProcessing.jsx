import { Divider, Space, Typography } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { ProcessingAutomatedPipelines } from "./processing/ProcessingAutomatedPipelines";
import { ProcessingCoverage } from "./processing/ProcessingCoverage";

/**
 * Base script for displaying project process page
 * @returns {JSX.Element}
 * @constructor
 */
export default function ProjectProcessing({ projectId }) {
  const { canManage } = useSelector((state) => state.project);

  return (
    <>
      <Typography.Title level={2}>{i18n("Processing.title")}</Typography.Title>
      <Space style={{ width: `100%` }} direction="vertical">
        <ProcessingCoverage projectId={projectId} canManage={canManage} />
        <Divider />
        <ProcessingAutomatedPipelines
          projectId={projectId}
          canManage={canManage}
        />
      </Space>
    </>
  );
}
