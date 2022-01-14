import { Link } from "@reach/router";
import { Menu } from "antd";
import React from "react";
import { useLocation } from "react-router-dom";

/**
 * Component to handle navigation within the project settings page
 * @returns {JSX.Element}
 * @constructor
 */
export default function SettingsNav({ basePath, showRemote = false, canManage = false }) {
  const location = useLocation();
  const [key, setKey] = React.useState();

  React.useEffect(() => {
    const path = location.pathname.split("/");
    setKey(path.pop());
  }, [location]);

  return (
    <Menu selectedKeys={[key]}>
      <Menu.Item key="details">
        <Link to={`${basePath}details`}>
          {i18n("project.settings.page.details")}
        </Link>
      </Menu.Item>
      <Menu.Item key="processing">
        <Link to="processing">{i18n("project.settings.page.processing")}</Link>
      </Menu.Item>
      <Menu.Item key="members">
        <Link to="members">{i18n("project.settings.page.members")}</Link>
      </Menu.Item>
      <Menu.Item key="groups">
        <Link to="groups">{i18n("project.settings.page.groups")}</Link>
      </Menu.Item>
      <Menu.Item key="metadata">
        <Link className="t-m-field-link" to="metadata/fields">
          {i18n("project.settings.page.metadata")}
        </Link>
      </Menu.Item>
      <Menu.Item key="associated">
        <Link to="associated">{i18n("project.settings.page.associated")}</Link>
      </Menu.Item>
      <Menu.Item key="references">
        <Link to="references">
          {i18n("project.settings.page.referenceFiles")}
        </Link>
      </Menu.Item>
      {showRemote && (
        <Menu.Item key="remote">
          <Link to="remote">{i18n("project.settings.page.remote")}</Link>
        </Menu.Item>
      )}
      {canManage && (
        <Menu.Item key="delete">
          <Link to="delete">{i18n("DeleteProject.title")}</Link>
        </Menu.Item>
      )}
    </Menu>
  );
}
