import React from "react";
import type { MenuProps } from "antd";
import { Button, Layout, Menu, Space } from "antd";
import { SPACE_LG } from "../../../styles/spacing";
import { theme } from "../../../utilities/theme-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { AnnouncementsSubMenu } from "./components/AnnouncementsSubMenu";
import { CartLink } from "./components/CartLink";
import { GlobalSearch } from "./components/GlobalSearch";
import "./MainNavigation.css";
import { UserOutlined } from "@ant-design/icons";
import styled from "styled-components";

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";
const isManager = isAdmin || window.TL._USER.systemRole === "ROLE_MANAGER";
const isTechnician = window.TL._USER.systemRole === "ROLE_TECHNICIAN";

const MainMenu = styled(Menu)`
  .ant-menu-item-only-child,
  .ant-menu-submenu a {
    color: hsl(0deg 0% 100% / 65%);
    .anticon svg {
      color: hsl(0deg 0% 100% / 65%);
    }

    &:hover {
      color: var(--grey-1);

      .anticon svg {
        color: var(--grey-1);
      }
    }
  }
`;

const menuItems: MenuProps["items"] = [
  {
    key: "projects",
    label: <a href={setBaseUrl(`/projects`)}>{i18n("nav.main.projects")}</a>,
    children: [
      {
        key: "projects:user",
        label: (
          <a href={setBaseUrl(`/projects`)}>{i18n("nav.main.project-list")}</a>
        ),
      },
      ...(isAdmin
        ? [
            {
              key: "projects:admin",
              label: (
                <a href={setBaseUrl(`/projects/all`)}>
                  {i18n("nav.main.project-list-all")}
                </a>
              ),
            },
          ]
        : []),
      {
        type: "divider",
      },
      {
        key: "projects:sync",
        label: (
          <a href={setBaseUrl("/projects/synchronize")}>
            {i18n("nav.main.project-sync")}
          </a>
        ),
      },
    ],
  },
  {
    key: "analyses",
    label: <a href={setBaseUrl(`/analysis`)}>{i18n("nav.main.analysis")}</a>,
    children: [
      {
        key: "analyses:user",
        label: (
          <a href={setBaseUrl(`/analysis`)}>
            {i18n("nav.main.analysis-admin-user")}
          </a>
        ),
      },
      ...(isAdmin
        ? [
            {
              key: "analyses:admin",
              label: (
                <a href={setBaseUrl("/analysis/all")}>
                  {i18n("nav.main.analysis-admin-all")}
                </a>
              ),
            },
          ]
        : []),
      {
        type: "divider",
      },
      {
        key: "analyses:output",
        label: (
          <a href={setBaseUrl("/analysis/user/analysis-outputs")}>
            {i18n("Analysis.outputFiles")}
          </a>
        ),
      },
    ],
  },
  ...(isAdmin || isManager
    ? [
        {
          key: "users",
          label: <a href={setBaseUrl("/users")}>{i18n("nav.main.users")}</a>,
          children: [
            {
              key: "users:users",
              label: (
                <a href={setBaseUrl("/users")}>{i18n("nav.main.users-list")}</a>
              ),
            },
            {
              key: "users:groups",
              label: (
                <a href={setBaseUrl("/groups")}>
                  {i18n("nav.main.groups-list")}
                </a>
              ),
            },
          ],
        },
      ]
    : []),
  ...(isTechnician
    ? [
        {
          key: "sequencing-runs",
          label: (
            <a href={setBaseUrl("/sequencing-runs")}>
              {i18n("nav.main.sequencing-runs")}
            </a>
          ),
        },
      ]
    : []),
  ...(isAdmin
    ? [
        {
          key: "remote-api",
          label: (
            <a href={setBaseUrl("/remote_api")}>
              {i18n("nav.main.remoteapis")}
            </a>
          ),
        },
      ]
    : []),
];

const toolsItems: MenuProps["items"] = [
  ...(isAdmin
    ? [
        {
          key: "admin:link",
          label: (
            <a className="t-admin-panel-btn" href={setBaseUrl("/admin")}>
              {i18n("MainNavigation.admin").toUpperCase()}
            </a>
          ),
        },
      ]
    : []),
  {
    key: "cart",
    label: <CartLink />,
  },
  {
    key: "announcements",
    label: <AnnouncementsSubMenu />,
  },
  {
    key: "account",
    label: <UserOutlined />,
    children: [
      {
        key: "account:user",
        label: (
          <a href={setBaseUrl(`/users/current`)}>{i18n("nav.main.account")}</a>
        ),
      },
      {
        key: "account:help",
        label: i18n("nav.main.help"),
        children: [
          {
            key: "account:help:guide",
            label: (
              <a
                href="https://phac-nml.github.io/irida-documentation/user/user/"
                target="_blank"
                rel="noreferrer"
              >
                {i18n("nav.main.userguide")}
              </a>
            ),
          },
          ...(isAdmin
            ? [
                {
                  key: "account:help:admin",
                  label: (
                    <a
                      href="https://phac-nml.github.io/irida-documentation/user/administrator/"
                      target="_blank"
                      rel="noreferrer"
                    >
                      {i18n("nav.main.adminguide")}
                    </a>
                  ),
                },
              ]
            : []),
          {
            type: "divider",
          },
          {
            key: "account:help:website",
            label: (
              <a
                href="http://www.irida.ca"
                target="_blank"
                rel="noopener noreferrer"
              >
                {i18n("generic.irida.website")}
              </a>
            ),
          },
          {
            type: "divider",
          },
          {
            key: "account:help:version",
            label: i18n("irida.version"),
            disabled: true,
          },
        ],
      },
      {
        key: "account:logout",
        label: <a href={setBaseUrl("/logout")}>{i18n("nav.main.logout")}</a>,
      },
    ],
  },
];

export function MainNavigation() {
  const [isLargeScreen, setIsLargeScreen] = React.useState(
    window.innerWidth > 1050
  );

  // React.useEffect(() => {
  //   const handleResize = () => {
  //     setIsLargeScreen(window.innerWidth > 1050);
  //   };
  //   window.addEventListener("resize", handleResize);
  //   return () => window.removeEventListener("resize", handleResize);
  // }, []);

  console.log("RENDERING NAB");

  return (
    <Layout>
      <Layout.Header
        style={{ display: "flex", flexDirection: "row", alignItems: "center" }}
      >
        <a href={setBaseUrl("/")}>
          <img
            style={{ height: 28, width: 129, marginRight: SPACE_LG }}
            src={setBaseUrl(`/resources/img/irida_logo_${theme}.svg`)}
            alt={i18n("global.title")}
          />
        </a>
        {/*{isLargeScreen ? (*/}
        <MainMenu
          mode="horizontal"
          theme={theme}
          items={menuItems}
          style={{ width: 500 }}
        />
        <div style={{ flexGrow: 1 }} />
        <Space>
          <GlobalSearch />
        </Space>
        <MainMenu
          mode="horizontal"
          theme={theme}
          style={{ width: 360 }}
          items={toolsItems}
        />
      </Layout.Header>
    </Layout>
  );
}
