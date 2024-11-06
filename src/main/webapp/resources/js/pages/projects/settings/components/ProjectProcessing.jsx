import { Divider, Space, Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { useGetProjectDetailsQuery } from "../../../../apis/projects/project";
import { ProcessingAutomatedPipelines } from "./processing/ProcessingAutomatedPipelines";
import { ProcessingCoverage } from "./processing/ProcessingCoverage";

/**
 * Base script for displaying project process page
 * @returns {JSX.Element}
 * @constructor
 */
export default function ProjectProcessing() {
  const { projectId } = useParams();
  const { data: project = {} } = useGetProjectDetailsQuery(projectId);

  return (
    <>
      <Typography.Title level={2}>{i18n("Processing.title")}</Typography.Title>
      <Space style={{ width: `100%` }} direction="vertical">
        <ProcessingCoverage
          projectId={projectId}
          canManage={project.canManage}
        />
        <Divider />
        <ProcessingAutomatedPipelines
          projectId={projectId}
          canManage={project.canManage}
        />
      </Space>
    </>
  );
}
