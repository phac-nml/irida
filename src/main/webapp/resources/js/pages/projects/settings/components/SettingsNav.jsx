import { Link } from "@reach/router";
import { Menu } from "antd";
import React from "react";

/**
 * Component to handle navigation within the project settings page
 * @param {string} path - the current page name
 * @returns {JSX.Element}
 * @constructor
 */
export default function SettingsNav({ path }) {
  const [key, setKey] = React.useState();

  React.useEffect(() => {
    setKey(path.split("/")[0]);
  }, [path]);

  return (
    <Menu selectedKeys={[key]} style={{ height: `100%` }}>
      <Menu.Item key="details">
        <Link to="details">{i18n("project.settings.page.details")}</Link>
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
    </Menu>
  );
}
