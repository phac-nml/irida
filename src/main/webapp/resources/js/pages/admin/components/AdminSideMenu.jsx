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

import { useState } from "react";
const DEFAULT_URL = setBaseUrl("/admin");

const { Sider } = Layout;

const AdminSideMenu = () => {
  const [selectedKeys, setSelectedKeys] = useState("statistics");

  const items = [
    {
      key: "statistics",
      label: (
        <Link
          to={`${DEFAULT_URL}`}
          onClick={() => setSelectedKeys("statistics")}
          className={"t-admin-statistics-link"}
        >
          {i18n("AdminPanel.statistics")}
        </Link>
      ),
    },
    {
      key: "users-sub",
      label: i18n("AdminPanel.users"),
      children: [
        {
          key: "users",
          label: (
            <Link
              onClick={() => setSelectedKeys("users")}
              to={`${DEFAULT_URL}/${ADMIN.USERS}`}
              className={"t-admin-users-link"}
            >
              {i18n("AdminPanel.userList")}
            </Link>
          ),
        },
        {
          key: "groups",
          label: (
            <Link
              onClick={() => setSelectedKeys("groups")}
              to={`${DEFAULT_URL}/${ADMIN.USERGROUPS}/list`}
              className={"t-admin-groups-link"}
            >
              {i18n("AdminPanel.userGroupList")}
            </Link>
          ),
        },
      ],
    },
    {
      key: "clients",
      label: (
        <Link
          onClick={() => setSelectedKeys("clients")}
          to={`${DEFAULT_URL}/${ADMIN.CLIENTS}`}
          className={"t-admin-clients-link"}
        >
          {i18n("AdminPanel.clients")}
        </Link>
      ),
    },
    {
      key: "remote_api",
      label: (
        <Link
          onClick={() => setSelectedKeys("remote_api")}
          to={`${DEFAULT_URL}/${ADMIN.REMOTEAPI}`}
          className={"t-admin-remote-api-link"}
        >
          {i18n("AdminPanel.remoteApi")}
        </Link>
      ),
    },
    {
      key: "sequencing-runs",
      label: (
        <Link
          onClick={() => setSelectedKeys("sequencing-runs")}
          to={`${DEFAULT_URL}/${ADMIN.SEQUENCINGRUNS}`}
          className={"t-admin-sequencing-runs-link"}
        >
          {i18n("AdminPanel.sequencingRuns")}
        </Link>
      ),
    },
    {
      key: "ncbi_exports",
      label: (
        <Link
          onClick={() => setSelectedKeys("ncbi_exports")}
          to={`${DEFAULT_URL}/${ADMIN.NCBIEXPORTS}`}
          className={"t-admin-ncbi-exports-link"}
        >
          {i18n("AdminPanel.ncbiExports")}
        </Link>
      ),
    },
    {
      key: "announcements",
      label: (
        <Link
          onClick={() => setSelectedKeys("announcements")}
          to={`${DEFAULT_URL}/${ADMIN.ANNOUNCEMENTS}`}
          className={"t-admin-announcements-link"}
        >
          {i18n("AdminPanel.announcements")}
        </Link>
      ),
    },
  ];

  return (
    <Sider width={}>
      <section>
        <Link to={DEFAULT_URL}>
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
          items={items}
        />
      </section>
    </Sider>
  );
};

export default AdminSideMenu;
