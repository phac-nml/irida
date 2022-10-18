/*
 * This file renders the AdminHeader component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";

import { Layout, Menu, Space } from "antd";
import type { MenuProps } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { grey1 } from "../../../styles/colors";
import { IconHome, IconUser } from "../../../components/icons/Icons";
import { UserOutlined } from "@ant-design/icons";

const { SubMenu } = Menu;

const { Header } = Layout;

const menuItems: MenuProps["items"] = [
  {
    key: "admin:home",
    label: i18n("AdminPanel.return"),
  },
  {
    key: "admin:account",
    label: <UserOutlined />, //i18n("AdminPanel.profile"),
  },
];

export default function AdminHeader() {
  const HOME_URL = setBaseUrl("/");
  const ACCOUNT_URL = setBaseUrl("/users/current");
  const LOGOUT_URL = setBaseUrl("/logout");

  // >
  // <Menu.Item icon={<IconHome />} key="return">
  //       <a href={`${HOME_URL}`}>{}</a>
  // </Menu.Item>
  //   <SubMenu icon={<IconUser />} key="profile">
  //     <Menu.Item key="account">
  //       <a href={`${ACCOUNT_URL}`}>{i18n("AdminPanel.account")}</a>
  //     </Menu.Item>
  //     <Menu.Item key="logout">
  //       <a href={`${LOGOUT_URL}`}>{i18n("AdminPanel.logout")}</a>
  //     </Menu.Item>
  //   </SubMenu>
  // </Menu>

  // The following renders the AdminPanelHeader component
  return (
    <Header
      style={{
        backgroundColor: "orange",
        display: "flex",
      }}
    >
      <Menu
        items={menuItems}
        mode="horizontal"
        theme="light"
        style={{ minWidth: 0, flex: "auto" }}
      />
    </Header>
  );
}
