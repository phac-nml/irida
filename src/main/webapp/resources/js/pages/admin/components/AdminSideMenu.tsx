/*
 * This file renders the AdminSideMenu component
 */
import type { MenuProps } from "antd";
import { Layout, Menu } from "antd";
/*
 * The following import statements makes available
 * all the elements required by the component
 */
import React, { useEffect } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { setBaseUrl } from "../../../utilities/url-utilities";
import * as path from "path";

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
    navigate(
      key === "admin:statistics" ? DEFAULT_URL : `${DEFAULT_URL}/${path}`
    );
  };

  useEffect(() => {
    let path = location.pathname.split("/").pop();
    if (path) {
      path = path === "admin" ? "statistics" : path;
      setSelectedKeys([`admin:${path}`]);
    }
  }, [location.pathname]);

  // The following renders the AdminPanelSideMenu component
  return (
    <Sider width={220}>
      <section>
        <Link
          style={{
            display: "flex",
            height: 64,
            alignItems: "center",
            justifyContent: "center",
            borderBottom: `1px solid hsl(216deg 20% 95%)`,
          }}
          to={setBaseUrl("/")}
          title={i18n("AdminPanel.logo-tooltip")}
        >
          <img
            height="28"
            width="129"
            src={setBaseUrl("/resources/img/irida_logo_dark.svg")}
            alt={i18n("AdminPanel.logo-alt")}
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
