/*
 * This file renders the AdminHeader component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";

import { Layout, Menu } from "antd";
import { accountMenu } from "../../../components/menu/AccountMenu";

const { Header } = Layout;

export default function AdminHeader() {
  // The following renders the AdminPanelHeader component
  return (
    <Header
      style={{
        backgroundColor: `#ffffff`,
        display: "grid",
        justifyContent: "end",
      }}
    >
      <Menu
        items={[accountMenu]}
        mode="horizontal"
        theme="light"
        style={{ width: 60 }}
      />
    </Header>
  );
}
