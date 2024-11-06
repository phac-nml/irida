/**
 * @File component responsible for the layout of the associated projects page.
 */
import { Space, Typography } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import ViewAssociatedProjects from "./associated/ViewAssociatedProjects";

export default function AssociatedProjects(): JSX.Element {
  const { projectId } = useParams();
  return (
    <>
      <Typography.Title level={2}>
        {i18n("AssociatedProjects.title")}
      </Typography.Title>
      <Space size="small" direction="vertical" style={{ width: `100%` }}>
        <Typography.Text type="secondary">
          {i18n("AssociatedProjects.subTitle")}
        </Typography.Text>
        {projectId && (
          <ViewAssociatedProjects projectId={parseInt(projectId)} />
        )}
      </Space>
    </>
  );
}
