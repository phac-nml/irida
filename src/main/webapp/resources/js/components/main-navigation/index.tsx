import React from "react";
import { Menu, Space } from "antd";
import GlobalSearch from "./components/GlobalSearch";
import {
  CONTEXT_PATH,
  ROUTE_ADMIN,
  ROUTE_ANALYSES,
  ROUTE_ANALYSES_ALL,
  ROUTE_ANALYSES_OUTPUT,
  ROUTE_HOME,
  ROUTE_PROJECTS_ALL,
  ROUTE_PROJECTS_PERSONAL,
  ROUTE_PROJECTS_SYNC,
  ROUTE_REMOTE_API,
  ROUTE_SEQUENCING_RUNS,
  ROUTE_USER_GROUPS,
  ROUTE_USERS,
} from "../../data/routes";
import { theme } from "../../utilities/theme-utilities";
import "./index.css";
import CartLink from "./components/CartLink";

type MenuItem = {
  key: string;
  label?: string | JSX.Element;
  type?: "divider";
  children?: MenuItem[];
};

const menuItems: MenuItem[] = [
  {
    key: `nav-projects`,
    label: <a href={ROUTE_PROJECTS_PERSONAL}>{i18n("nav.main.projects")}</a>,
    children: [
      {
        key: `nav-projects-personal`,
        label: (
          <a href={ROUTE_PROJECTS_PERSONAL}>{i18n("nav.main.project-list")}</a>
        ),
      },
      ...[
        // ADMIN ONLY
        {
          key: `nav-projects-all`,
          label: (
            <a href={ROUTE_PROJECTS_ALL}>{i18n("nav.main.project-list-all")}</a>
          ),
        },
      ],
      { type: `divider`, key: `nav-div-1` },
      {
        key: `nav-projects-sync`,
        label: (
          <a href={ROUTE_PROJECTS_SYNC}>{i18n("nav.main.project-sync")}</a>
        ),
      },
    ],
  },
  {
    key: `nav-analyses`,
    label: <a href={ROUTE_ANALYSES}>{i18n("nav.main.analysis")}</a>,
    children: [
      {
        key: `nav-analyses-personal`,
        label: (
          <a href={ROUTE_ANALYSES}>{i18n("nav.main.analysis-admin-user")}</a>
        ),
      },
      ...[
        // ADMIN ONLY
        {
          key: `nav-analyses-all`,
          label: (
            <a href={ROUTE_ANALYSES_ALL}>
              {i18n("nav.main.analysis-admin-all")}
            </a>
          ),
        },
      ],
      { type: `divider`, key: `nav-div-2` },
      {
        key: `nav-analyses-output`,
        label: (
          <a href={ROUTE_ANALYSES_OUTPUT}>{i18n("Analysis.outputFiles")}</a>
        ),
      },
    ],
  },
  // not admin but is manager
  ...[
    {
      key: `nav-users`,
      label: <a href={ROUTE_USERS}>{i18n("nav.main.users")}</a>,
      children: [
        {
          key: `nav-users-list`,
          label: <a href={ROUTE_USERS}>{i18n("nav.main.users-list")}</a>,
        },
        {
          key: `nav-user-groups`,
          label: <a href={ROUTE_USER_GROUPS}>{i18n("nav.main.groups-list")}</a>,
        },
      ],
    },
  ],
  // TECHNICIANS only
  ...[
    {
      key: `nav-sequencing`,
      label: (
        <a href={ROUTE_SEQUENCING_RUNS}>{i18n("nav.main.sequencing-runs")}</a>
      ),
    },
  ],
  // NOT ADMINS
  ...[
    {
      key: `nav-remote-api`,
      label: <a href={ROUTE_REMOTE_API}>{i18n("nav.main.remoteapis")}</a>,
    },
  ],
  // ADMIN ONLY
  ...[
    {
      key: `nav-admin`,
      label: <a href={ROUTE_ADMIN}>{i18n("MainNavigation.admin")}</a>,
    },
  ],
];

function renderMenuItem(item: MenuItem): JSX.Element {
  if (item.type === `divider`) {
    return <Menu.Divider key={item.key} />;
  } else if (!item.children) {
    return <Menu.Item key={item.key}>{item.label}</Menu.Item>;
  } else {
    return (
      <Menu.SubMenu key={item.key} title={item.label}>
        {item.children.map(renderMenuItem)}
      </Menu.SubMenu>
    );
  }
}

export default function MainNavigation(): JSX.Element {
  return (
    <>
      <a href={ROUTE_HOME}>
        <img
          className={"nav-logo"}
          src={`${CONTEXT_PATH}/resources/img/irida_logo_${theme}.svg`}
          alt={i18n("global.title")}
        />
      </a>
      <div
        style={{
          display: "flex",
          flexDirection: "row",
          justifyContent: "space-between",
        }}
      >
        <Menu className={"main-nav"} theme={theme} mode={"horizontal"}>
          {menuItems.map(renderMenuItem)}
        </Menu>
        <Space direction={"horizontal"}>
          <GlobalSearch />
          <CartLink />
        </Space>
      </div>
    </>
  );
}
