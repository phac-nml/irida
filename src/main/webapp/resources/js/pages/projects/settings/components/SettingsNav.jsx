import { Menu } from "antd";
import React from "react";
import { Link, useLocation } from "react-router-dom";

/**
 * Component to handle navigation within the project settings page
 * @returns {JSX.Element}
 * @constructor
 */
export default function SettingsNav({
  basePath,
  showRemote = false,
  canManage = false,
}) {
  const location = useLocation();
  const [key, setKey] = React.useState(() => {
    const keyRegex = /\/projects\/\d+\/settings\/(?<path>[\w_-]+)/;
    const found = location.pathname.match(keyRegex);
    if (found) {
      return found.groups.path;
    }
    return "details";
  });

  return (
    <Menu selectedKeys={[key]} onClick={(e) => setKey(e.key)}>
      <Menu.Item key="details">
        <Link to={basePath}>{i18n("project.settings.page.details")}</Link>
      </Menu.Item>
      <Menu.Item key="processing">
        <Link to={`${basePath}processing`}>
          {i18n("project.settings.page.processing")}
        </Link>
      </Menu.Item>
      <Menu.Item key="members">
        <Link to={`${basePath}members`}>
          {i18n("project.settings.page.members")}
        </Link>
      </Menu.Item>
      <Menu.Item key="groups">
        <Link to={`${basePath}groups`}>
          {i18n("project.settings.page.groups")}
        </Link>
      </Menu.Item>
      <Menu.Item key="metadata">
        <Link className="t-m-field-link" to={`${basePath}metadata/fields`}>
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
