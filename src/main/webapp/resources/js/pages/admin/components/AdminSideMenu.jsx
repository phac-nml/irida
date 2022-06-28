/*
 * This file renders the AdminSideMenu component
 */
import { Layout, Menu } from "antd";
/*
 * The following import statements makes available
 * all the elements required by the component
 */
import React from "react";
import { Link } from "react-router-dom";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ADMIN } from "../routes";

const { SubMenu } = Menu;
const { Sider } = Layout;
export default function AdminHeader() {
  const [selectedKeys, setSelectedKeys] = React.useState(() => {
    // Grab it from the URL, look for where you are which
    // should be right after the admin path
    const regExp = /admin\/(\w*)\/?/i;
    const found = window.location.href.match(regExp);
    return found !== null ? found[1] : "statistics";
  });
  const DEFAULT_URL = setBaseUrl("/admin");
  // The following renders the AdminPanelSideMenu component
  return (
    <Sider width={220}>
      <section>
        <Link
          style={{ paddingLeft: 10, paddingRight: 10 }}
          to={`${DEFAULT_URL}/${ADMIN.USERS}`}
        >
          <img
            height="64"
            width="200"
            src={setBaseUrl("/resources/img/irida_logo_dark.svg")}
          />
        </Link>
        <Menu
          className={"t-admin-side-menu"}
          style={{ height: "calc(100vh - 65px)" }}
          theme={"dark"}
          mode={"inline"}
          selectedKeys={[selectedKeys]}
        >
          <Menu.Item key="statistics">
            <Link
              to={`${DEFAULT_URL}`}
              onClick={() => setSelectedKeys("statistics")}
              className={"t-admin-statistics-link"}
            >
              {i18n("AdminPanel.statistics")}
            </Link>
          </Menu.Item>
          <SubMenu
            title={i18n("AdminPanel.users")}
            className={"t-admin-users-submenu"}
            key="users-sub"
          >
            <Menu.Item key="users">
              <Link
                onClick={() => setSelectedKeys("users")}
                to={`${DEFAULT_URL}/${ADMIN.USERS}`}
                className={"t-admin-users-link"}
              >
                {i18n("AdminPanel.userList")}
              </Link>
            </Menu.Item>
            <Menu.Item key="groups">
              <Link
                onClick={() => setSelectedKeys("groups")}
                to={`${DEFAULT_URL}/${ADMIN.USERGROUPS}/list`}
                className={"t-admin-groups-link"}
              >
                {i18n("AdminPanel.userGroupList")}
              </Link>
            </Menu.Item>
          </SubMenu>
          <Menu.Item key="clients">
            <Link
              onClick={() => setSelectedKeys("clients")}
              to={`${DEFAULT_URL}/${ADMIN.CLIENTS}`}
              className={"t-admin-clients-link"}
            >
              {i18n("AdminPanel.clients")}
            </Link>
          </Menu.Item>
          <Menu.Item key="remote_api">
            <Link
              onClick={() => setSelectedKeys("remote_api")}
              to={`${DEFAULT_URL}/${ADMIN.REMOTEAPI}`}
              className={"t-admin-remote-api-link"}
            >
              {i18n("AdminPanel.remoteApi")}
            </Link>
          </Menu.Item>
          <Menu.Item key="sequencing-runs">
            <Link
              onClick={() => setSelectedKeys("sequencing-runs")}
              to={`${DEFAULT_URL}/${ADMIN.SEQUENCINGRUNS}`}
              className={"t-admin-sequencing-runs-link"}
            >
              {i18n("AdminPanel.sequencingRuns")}
            </Link>
          </Menu.Item>
          <Menu.Item key="ncbi_exports">
            <Link
              onClick={() => setSelectedKeys("ncbi_exports")}
              to={`${DEFAULT_URL}/${ADMIN.NCBIEXPORTS}`}
              className={"t-admin-ncbi-exports-link"}
            >
              {i18n("AdminPanel.ncbiExports")}
            </Link>
          </Menu.Item>
          <Menu.Item key="announcements">
            <Link
              onClick={() => setSelectedKeys("announcements")}
              to={`${DEFAULT_URL}/${ADMIN.ANNOUNCEMENTS}`}
              className={"t-admin-announcements-link"}
            >
              {i18n("AdminPanel.announcements")}
            </Link>
          </Menu.Item>
        </Menu>
      </section>
    </Sider>
  );
}
