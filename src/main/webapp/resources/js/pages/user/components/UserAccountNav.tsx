import React from "react";
import { useLocation, useNavigate } from "react-router-dom";
import type { MenuProps } from "antd";
import { Menu } from "antd";

const menuItems: MenuProps["items"] = [
  {
    key: "account:details",
    label: i18n("UserAccountNav.menu.details"),
  },

  {
    key: "account:projects",
    label: i18n("UserAccountNav.menu.projects"),
  },
  {
    key: "account:security",
    label: i18n("UserAccountNav.menu.security"),
  },
];

/**
 * React component to display the user account navigation.
 * @returns {*}
 * @constructor
 */
export default function UserAccountNav(): JSX.Element {
  const location = useLocation();
  const navigate = useNavigate();
  const [selectedKeys, setSelectedKey] = React.useState<string[]>(() => {
    const path = location.pathname.split("/").pop();
    return [`account:${path}`];
  });

  const onClick: MenuProps["onClick"] = ({ key }) => {
    const [, path] = key.split(":");
    setSelectedKey([key]);
    navigate(path);
  };

  return (
    <Menu
      items={menuItems}
      mode="inline"
      onClick={onClick}
      selectedKeys={selectedKeys}
      style={{ height: "100%" }}
    />
  );
}
