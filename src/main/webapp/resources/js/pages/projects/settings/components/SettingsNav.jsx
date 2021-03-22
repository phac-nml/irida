import React from "react";
import { Menu } from "antd";
import { Link } from "@reach/router";

export default function SettingsNav({ path }) {
  return (
    <Menu selectedKeys={[path]}>
      <Menu.Item key="details">
        <Link to="details">{i18n("project.settings.page.details")}</Link>
      </Menu.Item>
    </Menu>
  );
}
