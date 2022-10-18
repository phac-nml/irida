/*
 * This file renders the AdminSideMenu component
 */
import type { MenuProps } from "antd";
import { Layout, Menu } from "antd";
/*
 * The following import statements makes available
 * all the elements required by the component
 */
import React from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ADMIN } from "../routes";

const { SubMenu } = Menu;
const { Sider } = Layout;
export default function AdminHeader() {
  const navigate = useNavigate();
  const location = useLocation();

  const [selectedKeys, setSelectedKeys] = React.useState<string[]>(() => {
    // Grab it from the URL, look for where you are which
    // should be right after the admin path
    const regExp = /admin\/(\w*)\/?/i;
    const found = location.pathname.match(regExp);
    if (found === null || found[1] === "") {
      return ["admin:statistics"];
    } else {
      return [`admin:${found[1]}`];
    }
  });
  const DEFAULT_URL = setBaseUrl("/admin");

  const menuItems: MenuProps["items"] = [
    {
      key: "admin:statistics",
      label: i18n("AdminPanel.statistics"),
    },
    {
      key: "admin:users",
      label: i18n("AdminPanel.userList"),
    },
    {
      key: "admin:groups",
      label: i18n("AdminPanel.userGroupList"),
    },
    {
      key: "admin:clients",
      label: i18n("AdminPanel.clients"),
    },
    {
      key: "admin:remote-api",
      label: i18n("AdminPanel.remoteApi"),
    },
    {
      key: "admin:sequencing-runs",
      label: i18n("AdminPanel.sequencingRuns"),
    },
    {
      key: "admin:ncbi-exports",
      label: i18n("AdminPanel.ncbiExports"),
    },
    {
      key: "admin:announcements",
      label: i18n("AdminPanel.announcements"),
    },
  ];

  const onClick: MenuProps["onClick"] = ({ key }) => {
    const [, path] = key.split(":");
    console.log(path);
    navigate(path === "statistics" ? DEFAULT_URL : `${DEFAULT_URL}/${path}`);
    setSelectedKeys([key]);
  };

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
          items={menuItems}
          onClick={onClick}
          selectedKeys={selectedKeys}
          style={{ height: "calc(100vh - 65px)" }}
          theme={"dark"}
        />
      </section>
    </Sider>
  );
}
