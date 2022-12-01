import React, { useMemo } from "react";
import { Menu, Space } from "antd";
import GlobalSearch from "./components/GlobalSearch";
import {
  CONTEXT_PATH,
  ROUTE_ADMIN,
  ROUTE_ANALYSES,
  ROUTE_ANALYSES_ALL,
  ROUTE_ANALYSES_OUTPUT,
  ROUTE_HOME,
  ROUTE_LOGOUT,
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
import CurrentUser from "./components/CurrentUser";
import type { MenuItem } from "../../types/ant-design";
import { renderMenuItem } from "../ant.design/menu-utilities";
import { useGetCurrentUserQuery } from "../../redux/endpoints/user";
import { setBaseUrl } from "../../utilities/url-utilities";
import AnnouncementLink from "./components/AnnouncementLink";

/**
 * React component to render the main navigation component at the top of the page.
 * @constructor
 */
export default function MainNavigation(): JSX.Element {
  const { data: user = {}, isSuccess } = useGetCurrentUserQuery(undefined, {});

  const leftMenuItems: MenuItem[] = useMemo(
    () => [
      {
        key: `nav-projects`,
        label: (
          <a href={ROUTE_PROJECTS_PERSONAL}>{i18n("nav.main.projects")}</a>
        ),
        children: [
          {
            key: `nav-projects-personal`,
            label: (
              <a href={ROUTE_PROJECTS_PERSONAL}>
                {i18n("nav.main.project-list")}
              </a>
            ),
          },
          ...(isSuccess && user.admin
            ? [
                {
                  key: `nav-projects-all`,
                  label: (
                    <a href={ROUTE_PROJECTS_ALL}>
                      {i18n("nav.main.project-list-all")}
                    </a>
                  ),
                },
              ]
            : []),
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
              <a href={ROUTE_ANALYSES}>
                {i18n("nav.main.analysis-admin-user")}
              </a>
            ),
          },
          ...(isSuccess && user.admin
            ? [
                {
                  key: `nav-analyses-all`,
                  label: (
                    <a href={ROUTE_ANALYSES_ALL}>
                      {i18n("nav.main.analysis-admin-all")}
                    </a>
                  ),
                },
              ]
            : []),
          { type: `divider`, key: `nav-div-2` },
          {
            key: `nav-analyses-output`,
            label: (
              <a href={ROUTE_ANALYSES_OUTPUT}>{i18n("Analysis.outputFiles")}</a>
            ),
          },
        ],
      },
      ...(isSuccess && !user.admin && user.manager
        ? [
            {
              key: `nav-users`,
              label: <a href={ROUTE_USERS}>{i18n("nav.main.users")}</a>,
              children: [
                {
                  key: `nav-users-list`,
                  label: (
                    <a href={ROUTE_USERS}>{i18n("nav.main.users-list")}</a>
                  ),
                },
                {
                  key: `nav-user-groups`,
                  label: (
                    <a href={ROUTE_USER_GROUPS}>
                      {i18n("nav.main.groups-list")}
                    </a>
                  ),
                },
              ],
            },
          ]
        : []),
      ...(isSuccess && user.technician
        ? [
            {
              key: `nav-sequencing`,
              label: (
                <a href={ROUTE_SEQUENCING_RUNS}>
                  {i18n("nav.main.sequencing-runs")}
                </a>
              ),
            },
          ]
        : []),
      ...(isSuccess && !user.admin
        ? [
            {
              key: `nav-remote-api`,
              label: (
                <a href={ROUTE_REMOTE_API}>{i18n("nav.main.remoteapis")}</a>
              ),
            },
          ]
        : []),
      ...(isSuccess && user.admin
        ? [
            {
              key: `nav-admin`,
              label: <a href={ROUTE_ADMIN}>{i18n("MainNavigation.admin")}</a>,
            },
          ]
        : []),
    ],
    [isSuccess, user.admin]
  );

  const rightMenuItems: MenuItem[] = useMemo(
    () => [
      {
        key: `nav-cart`,
        label: <CartLink />,
      },
      {
        key: `nav-announcements`,
        label: <AnnouncementLink />,
      },
      {
        key: `nav-user`,
        label: <CurrentUser />,
        children: [
          {
            key: `nav-user-account`,
            label: (
              <a href={setBaseUrl(`/users/current`)}>
                {i18n("nav.main.account")}
              </a>
            ),
          },
          {
            key: `nav-guides`,
            label: `Guides`,
            children: [
              {
                key: `nav-user-guide`,
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
              ...(isSuccess && user.admin
                ? [
                    {
                      key: `nav-admin-guide`,
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
            ],
          },
          {
            type: "divider",
            key: "nav-account-divider",
          },
          {
            key: `nav-logout`,
            label: <a href={ROUTE_LOGOUT}>{i18n("nav.main.logout")}</a>,
          },
          {
            type: "divider",
            key: "nav-logout-divider",
          },
          {
            key: `nav-version`,
            disabled: true,
            label: i18n("irida.version"),
          },
        ],
      },
    ],
    [isSuccess, user?.admin]
  );

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
          {leftMenuItems.map(renderMenuItem)}
        </Menu>
        <Space direction={"horizontal"}>
          <GlobalSearch />
          <Menu className={"utils-nav"} theme={theme} mode={"horizontal"}>
            {rightMenuItems.map(renderMenuItem)}
          </Menu>
        </Space>
      </div>
    </>
  );
}
