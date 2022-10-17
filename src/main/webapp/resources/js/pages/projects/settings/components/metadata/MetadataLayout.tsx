import { Space, Typography } from "antd";
import React from "react";
import { Outlet, useParams } from "react-router-dom";

/**
 * Component for rendering the metadata fields and templates
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function MetadataLayout(): JSX.Element {
  const { projectId } = useParams();

  return (
    <>
      <Typography.Title level={2}>
        {i18n("project.settings.page.metadata")}
      </Typography.Title>
      <Space style={{ width: `100%` }} direction="vertical" size="large">
        <Outlet />
      </Space>
    </>
  );
}
