/*
 * This file renders the AdminSideMenu component
 */

/*
 * The following import statements makes available
 * all the elements required by the component
 */

import React from "react";

import { Layout, Menu } from "antd";
const { SubMenu } = Menu;
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Link, Location } from "@reach/router";
import { ADMIN } from "../routes";

const { Sider } = Layout;

export default function AdminHeader() {
  const DEFAULT_URL = setBaseUrl("/admin");
  const pathRegx = new RegExp(/([a-zA-Z]+)$/);

  // The following renders the AdminPanelSideMenu component
  return (
    <Sider>
      <Location>
        {props => {
          const keyname = props.location.pathname.match(pathRegx);
          return (
            <Menu style={{ height: '100vh' }} theme={"dark"} mode={"inline"}
                  selectedKeys={[keyname ? keyname[1] : ADMIN.STATISTICS]}>
              <Menu.Item key="logo">
                <Link to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`} className={"t-admin-stats-menu"}>
                  <img src="/resources/img/irida_logo_dark.svg"/>
                </Link>
              </Menu.Item>
              <Menu.Item key="statistics">
                <Link to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`}>
                  {i18n("admin.panel.statistics")}
                </Link>
              </Menu.Item>
              <SubMenu key="users" title={i18n("admin.panel.users")} className={"t-admin-users-sub-menu"}>
                <Menu.Item key="userList">
                  <Link to={`${DEFAULT_URL}/${ADMIN.USERS}`} className={"t-admin-users-menu"}>
                    {i18n("admin.panel.userList")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="groupList">
                  <Link to={`${DEFAULT_URL}/${ADMIN.GROUPS}`} className={"t-admin-groups-menu"}>
                    {i18n("admin.panel.groupList")}
                  </Link>
                </Menu.Item>
              </SubMenu>
            </Menu>
          );
        }}
      </Location>
    </Sider>
  );
}
