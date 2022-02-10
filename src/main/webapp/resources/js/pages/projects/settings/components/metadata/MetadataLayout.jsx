import { Menu, Space, Typography } from "antd";
import React from "react";
import { Link, Outlet, useParams } from "react-router-dom";
import { setBaseUrl } from "../../../../../utilities/url-utilities";

/**
 * Component for rendering the metadata fields and templates
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function MetadataLayout({ children, ...props }) {
  const { projectId } = useParams();
  const baseUrl = setBaseUrl(`/projects/${projectId}/settings/metadata`);
  return (
    <>
      <Typography.Title level={2}>
        {i18n("project.settings.page.metadata")}
      </Typography.Title>
      <Space style={{ width: `100%` }} direction="vertical" size="large">
        <Menu mode="horizontal" selectedKeys={[props["*"]]}>
          <Menu.Item key="fields" className="t-m-field-link">
            <Link to={`${baseUrl}/fields`}>{i18n("MetadataFields.title")}</Link>
          </Menu.Item>
          <Menu.Item key={`${baseUrl}/templates`} className="t-m-template-link">
            <Link to="templates">{i18n("ProjectMetadataTemplates.title")}</Link>
          </Menu.Item>
        </Menu>
        <Outlet />
      </Space>
    </>
  );
}
