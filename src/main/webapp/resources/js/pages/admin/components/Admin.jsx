

import { Layout, Menu } from "antd";
import { Link, Location, Router } from "@reach/router";
import { ADMIN } from "../routes";
import React, { Suspense, lazy, useContext } from "react";
import { ContentLoading } from "../../../components/loader";
const { SubMenu } = Menu;
import { setBaseUrl } from "../../../utilities/url-utilities";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import Button from "antd/es/button";
import { SETTINGS } from "../../analysis/routes";

const { Content, Sider, Header } = Layout;
import { IconUser, IconHome } from "../../../components/icons/Icons";

const AdminStatistics = lazy(() => import("./AdminStatistics"));
const AdminUsers = lazy(() => import("./AdminUsers"));

export default function Admin() {
  const HOME_URL = setBaseUrl("/dashboard");
  const DEFAULT_URL = setBaseUrl("/admin");
  const pathRegx = new RegExp(/([a-zA-Z]+)$/);

  return (
    <Layout>
        <Sider style={{ width: 200 }}>
          <Location>
            {props => {
              const keyname = props.location.pathname.match(pathRegx);
              return (
                <Menu style={{ height: '100vh'}} theme={"dark"} mode={"vertical"}
                      selectedKeys={[keyname ? keyname[1] : ADMIN.STATISTICS]}>
                  <Menu.Item key="image">
                    <Link to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`}>
                      <img src="/resources/img/irida_logo_dark.svg"/>
                    </Link>
                  </Menu.Item>
                  <Menu.Item key="statistics">
                    <Link to={`${DEFAULT_URL}/${ADMIN.STATISTICS}`}>
                      {i18n("admin.panel.statistics")}
                    </Link>
                  </Menu.Item>
                  <SubMenu key="users" title="Users">
                    <Menu.Item key="userList">
                      <Link to={`${DEFAULT_URL}/${ADMIN.USERS}`}>
                        {i18n("admin.panel.userList")}
                      </Link>
                    </Menu.Item>
                    <Menu.Item key="groupUserList">User Group List</Menu.Item>
                  </SubMenu>
                </Menu>
              );
            }}
          </Location>
        </Sider>
        <Layout>
          <Header style={{ backgroundColor: grey1, paddingRight: 0 }}>
            {/*<Button style={{ textAlign: 'right' }} type={"text"}>Profile</Button>*/}
            <Menu style={{ textAlign: 'right' }} theme={"light"} mode={"horizontal"}>
              <Menu.Item icon={<IconHome />} key="main-app">
                <Link to={`${HOME_URL}`}>
                  Return
                </Link>
              </Menu.Item>
              <SubMenu icon={<IconUser />} key="profile" title="Profile">
                <Menu.Item key="account">Account</Menu.Item>
                <Menu.Item key="logout">Logout</Menu.Item>
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
              </Router>
            </Suspense>
          </Content>
        </Layout>
    </Layout>
  );
}