import React from "react";
import { Link, useLocation } from "react-router-dom";
import { Menu } from "antd";

/**
 * React component to display the user account navigation.
 * @returns {*}
 * @constructor
 */
export default function UserAccountNav() {
  const lastElement = useLocation().pathname.split("/").pop();
  const defaultSelectedKey = lastElement.match(
    "details|projects|security|password"
  )
    ? lastElement
    : "details";

  return (
    <Menu mode="inline" defaultSelectedKeys={[defaultSelectedKey]}>
      <Menu.Item key="details">
        <Link to="details">{i18n("UserAccountNav.menu.details")}</Link>
      </Menu.Item>
      <Menu.Item key="projects">
        <Link to="projects">{i18n("UserAccountNav.menu.projects")}</Link>
      </Menu.Item>
      <Menu.ItemGroup
        key="security"
        title={i18n("UserAccountNav.menu.security")}
      >
        <Menu.Item key="password">
          <Link to="password">{i18n("UserAccountNav.menu.password")}</Link>
        </Menu.Item>
      </Menu.ItemGroup>
    </Menu>
  );
}
