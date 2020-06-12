/*
 * This file renders the details for the admin as well as,
 * lazily loads the Users and Statistics components (component
 * is only loaded when the corresponding tab is clicked
 */

/*
 * The following import statements makes available all the elements
 * required by the components encompassed within
 */

import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { ADMIN } from "../routes";
import React, { Suspense, lazy} from "react";
import { ContentLoading } from "../../../components/loader";
const { SubMenu } = Menu;
import { setBaseUrl } from "../../../utilities/url-utilities";
import { grey1 } from "../../../styles/colors";

const { Content, Sider, Header } = Layout;
import { IconUser, IconHome } from "../../../components/icons/Icons";

const AdminStatistics = lazy(() => import("./AdminStatistics"));
const AdminUsers = lazy(() => import("./AdminUsers"));
const AdminUserGroups = lazy(() => import("../../UserGroupsPage/components/UserGroupsPage"));

export default function Admin() {
  const HOME_URL = setBaseUrl("/");
  const DEFAULT_URL = setBaseUrl("/admin");
  const ACCOUNT_URL = setBaseUrl("/users/current")
  const LOGOUT_URL = setBaseUrl("/logout")
  const pathRegx = new RegExp(/([a-zA-Z]+)$/);

  /*
   * The following renders the tabs for statistics and users,
   * the components are only loaded if the corresponding
   * tab is clicked
   */
  return (
    <Layout>
        <Sider style={{ width: 200 }}>
          <Location>
            {props => {
              const keyname = props.location.pathname.match(pathRegx);
              return (
                <Menu style={{ height: '100vh' }} theme={"dark"} mode={"vertical"}
                      selectedKeys={[keyname ? keyname[1] : ADMIN.STATISTICS]}>
                  <Menu.Item key="logo">
                    <Link to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`}>
                      <img src="/resources/img/irida_logo_dark.svg"/>
                    </Link>
                  </Menu.Item>
                  <Menu.Item className={"t-admin-stats-menu"} key="statistics">
                    <Link to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`}>
                      {i18n("admin.panel.statistics")}
                    </Link>
                  </Menu.Item>
                  <SubMenu key="users" title={i18n("admin.panel.users")}>
                    <Menu.Item className={"t-admin-users-menu"} key="userList">
                      <Link to={`${DEFAULT_URL}/${ADMIN.USERS}`}>
                        {i18n("admin.panel.userList")}
                      </Link>
                    </Menu.Item>
                    <Menu.Item className={"t-admin-groups-menu"} key="groupList">
                      <Link to={`${DEFAULT_URL}/${ADMIN.GROUPS}`}>
                        {i18n("admin.panel.groupList")}
                      </Link>
                    </Menu.Item>
                  </SubMenu>
                </Menu>
              );
            }}
          </Location>
        </Sider>
        <Layout>
          <Header style={{ backgroundColor: grey1, paddingRight: 0 }}>
            <Menu style={{ textAlign: 'right' }} theme={"light"} mode={"horizontal"}>
              <Menu.Item icon={<IconHome />} key="return">
                <a href={`${HOME_URL}`}>
                  {i18n("admin.panel.return")}
                </a>
              </Menu.Item>
              <SubMenu icon={<IconUser />} key="profile" title={i18n("admin.panel.profile")}>
                <Menu.Item key="account">
                  <a href={`${ACCOUNT_URL}`}>
                    {i18n("admin.panel.account")}
                  </a>
                </Menu.Item>
                <Menu.Item key="logout">
                  <a href={`${LOGOUT_URL}`}>
                    {i18n("admin.panel.logout")}
                  </a>
                </Menu.Item>
              </SubMenu>
            </Menu>
          </Header>
          <Content>
            <Suspense fallback={<ContentLoading />}>
              <Router>
                <AdminStatistics
                  path={
                    `${DEFAULT_URL}/${ADMIN.STATISTICS}`
                  }
                  default
                />
                <AdminUsers
                  path={
                    `${DEFAULT_URL}/${ADMIN.USERS}`
                  }
                />
                <AdminUserGroups
                  path={
                    `${DEFAULT_URL}/${ADMIN.GROUPS}`
                  }
                />
              </Router>
            </Suspense>
          </Content>
        </Layout>
    </Layout>
  );
}