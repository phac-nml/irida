import React from "react";
import { Link, useLocation } from "react-router-dom";
import { Menu } from "antd";

/**
 * React component to display the user account navigation.
 * @returns {*}
 * @constructor
 */
export default function UserAccountNav() {
  const location = useLocation();
  const [selectedKeys, setSelectedKey] = React.useState("details");

  React.useEffect(() => {
    const lastElement = location.pathname.split("/").pop();
    if (lastElement.match("details|projects|security")) {
      setSelectedKey(lastElement);
    }
  }, [location]);

  return (
    <Menu mode="inline" selectedKeys={[selectedKeys]} style={{height: "100%"}}>
      <Menu.Item key="details">
        <Link to="details">{i18n("UserAccountNav.menu.details")}</Link>
      </Menu.Item>
      <Menu.Item key="projects">
        <Link to="projects">{i18n("UserAccountNav.menu.projects")}</Link>
      </Menu.Item>
      <Menu.Item key="security">
        <Link to="security">{i18n("UserAccountNav.menu.security")}</Link>
      </Menu.Item>
    </Menu>
  );
}
