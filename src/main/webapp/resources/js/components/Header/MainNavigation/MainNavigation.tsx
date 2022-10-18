import React from "react";
import type { MenuProps } from "antd";
import { Button, Layout, Menu, Space } from "antd";
import { theme } from "../../../utilities/theme-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { AnnouncementsSubMenu } from "./components/AnnouncementsSubMenu";
import { CartLink } from "./components/CartLink";
import { GlobalSearch } from "./components/GlobalSearch";
import "./MainNavigation.css";
import { UserOutlined } from "@ant-design/icons";

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";
const isManager = isAdmin || window.TL._USER.systemRole === "ROLE_MANAGER";
const isTechnician = window.TL._USER.systemRole === "ROLE_TECHNICIAN";
const inGalaxy = typeof window.GALAXY !== "undefined";

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
  ...(!isAdmin && isManager
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
  ...(!isAdmin
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

export function MainNavigation(): JSX.Element {
  const onClick: MenuProps["onClick"] = ({ key }) => {
    switch (key) {
      case "cart":
        window.location.href = setBaseUrl(
          `/cart/${inGalaxy ? "galaxy" : "pipelines"}`
        );
      case "announcements":
    }
  };

  return (
    <Layout>
      <Layout.Header className="main-navigation">
        <div
          style={{
            display: "flex",
            flexDirection: "row",
            minWidth: 0,
            flex: "auto",
          }}
        >
          <a href={setBaseUrl("/")}>
            <img
              style={{ height: 28, width: 129 }}
              src={setBaseUrl(`/resources/img/irida_logo_${theme}.svg`)}
              alt={i18n("global.title")}
            />
          </a>
          <Menu
            items={menuItems}
            mode="horizontal"
            theme={theme}
            style={{ width: `100%` }}
          />
        </div>
        <Space align="center">
          <GlobalSearch />
          {isAdmin && (
            <Button
              type="ghost"
              className="t-admin-panel-btn"
              href={setBaseUrl("/admin")}
            >
              {i18n("MainNavigation.admin")}
            </Button>
          )}
          <Menu
            onClick={onClick}
            disabledOverflow={true}
            items={toolsItems}
            mode="horizontal"
            theme={theme}
          />
        </Space>
      </Layout.Header>
    </Layout>
  );
}