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
            <section>
              <Link style={{ paddingLeft: 10, paddingRight: 10 }} to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`}>
                <img style={{ height: 64, width: 180 }} src="/resources/img/irida_logo_dark.svg"/>
              </Link>
              <Menu className={"t-admin-side-menu"} style={{ height: '100vh' }} theme={"dark"} mode={"inline"}
                    selectedKeys={[keyname ? keyname[1] : ADMIN.STATISTICS]}>
                <Menu.Item key="statistics">
                  <Link to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`} className={"t-admin-stats-link"}>
                    {i18n("AdminPanel.statistics")}
                  </Link>
                </Menu.Item>
                <SubMenu key="users" title={i18n("AdminPanel.users")} className={"t-admin-users-submenu"}>
                  <Menu.Item key="userList">
                    <Link to={`${DEFAULT_URL}/${ADMIN.USERS}`} className={"t-admin-users-link"}>
                      {i18n("AdminPanel.userList")}
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="userGroupsList">
                    <Link to={`${DEFAULT_URL}/${ADMIN.USERGROUPS}`} className={"t-admin-groups-link"}>
                      {i18n("AdminPanel.groupList")}
                    </Link>
                  </Menu.Item>
                </SubMenu>
              </Menu>
            </section>
          );
        }}
      </Location>
    </Sider>
  );
}
