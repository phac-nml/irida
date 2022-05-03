import { Menu, Space, Typography } from "antd";
import React from "react";
import { Link, useLocation, Outlet, useParams } from "react-router-dom";
import { setBaseUrl } from "../../../../../utilities/url-utilities";

/**
 * Component for rendering the metadata fields and templates
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function MetadataLayout({ children }) {
  const { projectId } = useParams();
  const baseUrl = setBaseUrl(`/projects/${projectId}/settings/metadata`);
  const location = useLocation();
  const [current, setCurrent] = React.useState("");
  const regex = /projects\/\d+\/settings\/metadata\/(?<path>\w+)/;

  React.useEffect(() => {
    const found = location.pathname.match(regex);

    if (found) {
      setCurrent(found.groups.path);
    } else {
      setCurrent("fields");
    }
  }, [location.pathname]);

  return (
    <>
      <Typography.Title level={2}>
        {i18n("project.settings.page.metadata")}
      </Typography.Title>
      <Space style={{ width: `100%` }} direction="vertical" size="large">
        <Menu mode="horizontal" selectedKeys={[current]}>
          <Menu.Item key="fields" className="t-m-field-link">
            <Link to={`${baseUrl}/fields`}>{i18n("MetadataFields.title")}</Link>
          </Menu.Item>
          <Menu.Item key={`templates`} className="t-m-template-link">
            <Link to={`${baseUrl}/templates`}>
              {i18n("ProjectMetadataTemplates.title")}
            </Link>
          </Menu.Item>
        </Menu>
        <Outlet />
      </Space>
    </>
  );
}
