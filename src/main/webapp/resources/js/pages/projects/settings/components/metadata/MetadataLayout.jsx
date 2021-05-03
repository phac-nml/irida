import { Link, Router } from "@reach/router";
import { Menu, Space } from "antd";
import React from "react";
import { useSelector } from "react-redux";

const MetadataFields = React.lazy(() => import("./MetadataFields"));
const MetadataTemplateManager = React.lazy(() =>
  import("./MetadataTemplateManager")
);
const MetadataTemplateMember = React.lazy(() =>
  import("./MetadataTemplateMember")
);
const MetadataTemplatesList = React.lazy(() =>
  import("./MetadataTemplatesList")
);

/**
 * Component for rendering the metadata fields and templates
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function MetadataLayout() {
  const { canManage } = useSelector((state) => state.project);
  const [selected, setSelected] = React.useState("fields");
  return (
    <Space style={{ width: `100%` }} direction="vertical">
      <Menu
        mode="horizontal"
        selectedKeys={[selected]}
        onClick={(e) => setSelected(e.key)}
      >
        <Menu.Item key="fields" className="t-m-field-link">
          <Link to="../metadata/fields">FIELDS</Link>
        </Menu.Item>
        <Menu.Item key="templates" className="t-m-template-link">
          <Link to="../metadata/templates">
            {i18n("ProjectMetadataTemplates.title")}
          </Link>
        </Menu.Item>
      </Menu>
      <Router>
        <MetadataFields path="/fields" />
        <MetadataTemplatesList path="/templates" />
        {canManage ? (
          <MetadataTemplateManager path="/templates/:id" />
        ) : (
          <MetadataTemplateMember path="/templates/:id" />
        )}
      </Router>
    </Space>
  );
}
