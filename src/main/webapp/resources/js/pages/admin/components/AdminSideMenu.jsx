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
  const pathRegx = new RegExp(/([a-zA-Z_])+\/?(\d*$)/);

  /**
   * Removes the end of the path that isn't part of the base route for the current admin page,
   * to allow the side menu item to be correctly selected for all paths on the route
   * @param {array} keyname holds a list of matches that represent a valid path name
   * @returns {string} key that corresponds to the selected admin side menu item
   */
  function parseKey(keyname) {
    return keyname[0].split('/')[0];
  }

  // The following renders the AdminPanelSideMenu component
  return (
    <Sider width={220}>
      <Location>
        {props => {
          const keyname = props.location.pathname.match(pathRegx);
          return (
            <section>
              <Link style={{ paddingLeft: 10, paddingRight: 10 }} to={`${DEFAULT_URL}/${ADMIN.USERS}`}>
                <img style={{ height: 64, width: 200 }} src={setBaseUrl("/resources/img/irida_logo_dark.svg")}/>
              </Link>
              <Menu className={"t-admin-side-menu"} style={{ height: '100vh' }} theme={"dark"} mode={"inline"}
                    selectedKeys={[keyname ? parseKey(keyname) : ADMIN.USERS]}>
                <SubMenu title={i18n("AdminPanel.users")} className={"t-admin-users-submenu"}>
                  <Menu.Item key="users">
                    <Link to={`${DEFAULT_URL}/${ADMIN.USERS}`} className={"t-admin-users-link"}>
                      {i18n("AdminPanel.userList")}
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="groups">
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
                <Menu.Item key="remote_api">
                  <Link to={`${DEFAULT_URL}/${ADMIN.REMOTEAPI}`} className={"t-admin-remote-api-link"}>
                    {i18n("AdminPanel.remoteApi")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="sequencing_runs">
                  <Link to={`${DEFAULT_URL}/${ADMIN.SEQUENCINGRUNS}`} className={"t-admin-sequencing-runs-link"}>
                    {i18n("AdminPanel.sequencingRuns")}
                  </Link>
                </Menu.Item>
                <Menu.Item key="ncbi_exports">
                  <Link to={`${DEFAULT_URL}/${ADMIN.NCBIEXPORTS}`} className={"t-admin-ncbi-exports-link"}>
                    {i18n("AdminPanel.ncbiExports")}
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
