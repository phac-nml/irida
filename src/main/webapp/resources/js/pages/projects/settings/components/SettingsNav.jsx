import React from "react";
import { Menu } from "antd";
import { Link } from "@reach/router";

export default function SettingsNav({ path }) {
  return (
    <Menu selectedKeys={[path]}>
      <Menu.Item key="details">
        <Link to="details">Details</Link>
      </Menu.Item>
    </Menu>
  );
}
