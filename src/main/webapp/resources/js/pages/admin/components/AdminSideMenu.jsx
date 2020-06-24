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
    <Sider width={220}>
      <Location>
        {props => {
          const keyname = props.location.pathname.match(pathRegx);
          return (
            <section>
              <Link style={{ paddingLeft: 10, paddingRight: 10 }} to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`}>
                <img style={{ height: 64, width: 200 }} src={setBaseUrl("/resources/img/irida_logo_dark.svg")}/>
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
                      {i18n("AdminPanel.userGroupList")}
                    </Link>
                  </Menu.Item>
                </SubMenu>
                <Menu.Item key="clients">
                  <Link to={`${DEFAULT_URL}/${ADMIN.CLIENTS}`} className={"t-admin-clients-link"}>
                    {i18n("AdminPanel.clients")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="remoteApi">
                  <Link to={`${DEFAULT_URL}/${ADMIN.REMOTEAPI}`} className={"t-admin-remote-api-link"}>
                    {i18n("AdminPanel.remoteApi")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="sequencingRuns">
                  <Link to={`${DEFAULT_URL}/${ADMIN.SEQUENCINGRUNS}`} className={"t-admin-sequencing-runs-link"}>
                    {i18n("AdminPanel.sequencingRuns")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="announcements">
                  <Link to={`${DEFAULT_URL}/${ADMIN.ANNOUNCEMENTS}`} className={"t-admin-announcements-link"}>
                    {i18n("AdminPanel.announcements")}
                  </Link>
                </Menu.Item>
              </Menu>
            </section>
          );
        }}
      </Location>
    </Sider>
  );
}
