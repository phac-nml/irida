/*
 * This file renders the AdminHeader component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";

import { Layout, Menu } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { grey1 } from "../../../styles/colors";
import { IconHome, IconUser } from "../../../components/icons/Icons";

const { SubMenu } = Menu;

const { Header } = Layout;

export default function AdminHeader() {
  const HOME_URL = setBaseUrl("/");
  const ACCOUNT_URL = setBaseUrl("/users/current");
  const LOGOUT_URL = setBaseUrl("/logout");

  // The following renders the AdminPanelHeader component
  return (
    <Header style={{ backgroundColor: grey1, paddingRight: 0 }}>
      <Menu style={{ textAlign: "right" }} theme={"light"} mode={"horizontal"}>
        <Menu.Item icon={<IconHome />} key="return">
          <a href={`${HOME_URL}`}>{i18n("AdminPanel.return")}</a>
        </Menu.Item>
        <SubMenu
          icon={<IconUser />}
          key="profile"
          title={i18n("AdminPanel.profile")}
        >
          <Menu.Item key="account">
            <a href={`${ACCOUNT_URL}`}>{i18n("AdminPanel.account")}</a>
          </Menu.Item>
          <Menu.Item key="logout">
            <a href={`${LOGOUT_URL}`}>{i18n("AdminPanel.logout")}</a>
          </Menu.Item>
        </SubMenu>
      </Menu>
    </Header>
  );
}
