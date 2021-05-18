import { Link } from "@reach/router";
import { Menu, Space, Typography } from "antd";
import React from "react";

/**
 * Component for rendering the metadata fields and templates
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function MetadataLayout({ children, ...props }) {
  return (
    <>
      <Typography.Title level={2}>
        {i18n("project.settings.page.metadata")}
      </Typography.Title>
      <Space style={{ width: `100%` }} direction="vertical" size="large">
        <Menu mode="horizontal" selectedKeys={[props["*"]]}>
          <Menu.Item key="fields" className="t-m-field-link">
            <Link to="fields">{i18n("MetadataFields.title")}</Link>
          </Menu.Item>
          <Menu.Item key="templates" className="t-m-template-link">
            <Link to="templates">{i18n("ProjectMetadataTemplates.title")}</Link>
          </Menu.Item>
        </Menu>
        <div>{children}</div>
      </Space>
    </>
  );
}
