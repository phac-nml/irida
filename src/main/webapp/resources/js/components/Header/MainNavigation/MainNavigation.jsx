import React from "react";
import { Button, Layout, Menu } from "antd";
import { SPACE_LG, SPACE_MD } from "../../../styles/spacing";
import { theme } from "../../../utilities/theme-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { IconUser } from "../../icons/Icons";
import { AnnouncementsSubMenu } from "./components/AnnouncementsSubMenu";
import { CartLink } from "./components/CartLink";
import { GlobalSearch } from "./components/GlobalSearch";
import "./MainNavigation.css";

const { Header } = Layout;

const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";
const isManager = isAdmin || window.TL._USER.systemRole === "ROLE_MANAGER";
const isTechnician = window.TL._USER.systemRole === "ROLE_TECHNICIAN";

export function MainNavigation() {
  const [isLargeScreen, setIsLargeScreen] = React.useState(
    window.innerWidth > 1050
  );

  React.useEffect(() => {
    const handleResize = () => {
      setIsLargeScreen(window.innerWidth > 1050);
    };
    window.addEventListener("resize", handleResize);
    return () => window.removeEventListener("resize", handleResize);
  }, []);

  return (
    <Header className="main-navigation">
      <a href={setBaseUrl("/")}>
        <img
          style={{ height: 28, width: 129, marginRight: SPACE_LG }}
          src={setBaseUrl(`/resources/img/irida_logo_${theme}.svg`)}
          alt={i18n("global.title")}
        />
      </a>
      {isLargeScreen ? (
        <Menu
          theme={theme}
          mode="horizontal"
          style={{
            display: "inline-block",
            minWidth: 400,
          }}
          items={[
            {
              key: "projects",
              label: <a href={setBaseUrl(`/projects`)}>{i18n("nav.main.projects")}</a>,
              children: [
                {
                  key: "project:list",
                  label: <a href={setBaseUrl(`/projects`)}>{i18n("nav.main.project-list")}</a>
                },
                isAdmin && {
                  key: "project:all",
                  label: <a href={setBaseUrl(`/projects/all`)}>{i18n("nav.main.project-list-all")}</a>
                },
                { type: 'divider' },
                {
                  key: "project:sync",
                  label: <a href={setBaseUrl("/projects/synchronize")}>{i18n("nav.main.project-sync")}</a>
                }
              ].filter(Boolean)
            },
            {
              key: "analysis",
              label: <a href={setBaseUrl(`/analysis`)}>{i18n("nav.main.analysis")}</a>,
              children: [
                {
                  key: "analyses:user",
                  label: <a href={setBaseUrl(`/analysis`)}>{i18n("nav.main.analysis-admin-user")}</a>
                },
                isAdmin && {
                  key: "analyses:all",
                  label: <a href={setBaseUrl("/analysis/all")}>{i18n("nav.main.analysis-admin-all")}</a>
                },
                { type: 'divider' },
                {
                  key: "analyses:output",
                  label: <a href={setBaseUrl("/analysis/user/analysis-outputs")}>{i18n("Analysis.outputFiles")}</a>
                }
              ].filter(Boolean)
            },
            !isAdmin && isManager && {
              key: "users",
              label: <a href={setBaseUrl("/users")}>{i18n("nav.main.users")}</a>,
              children: [
                {
                  key: "user:users",
                  label: <a href={setBaseUrl("/users")}>{i18n("nav.main.users-list")}</a>
                },
                {
                  key: "user:groups",
                  label: <a href={setBaseUrl("/groups")}>{i18n("nav.main.groups-list")}</a>
                }
              ]
            },
            isTechnician && {
              key: "sequencing-runs",
              label: <Button type="link" href={setBaseUrl("/sequencing-runs")}>{i18n("nav.main.sequencing-runs")}</Button>
            },
            !isAdmin && {
              key: "remote_api",
              label: <Button type="link" href={setBaseUrl("/remote_api")}>{i18n("nav.main.remoteapis")}</Button>
            }
          ].filter(Boolean)}
        />
      ) : (
        <Menu
          theme={theme}
          mode="horizontal"
          style={{
            display: "inline-block",
            width: 100,
          }}
          items={[
            {
              key: "projects",
              label: <a href={setBaseUrl(`/projects`)}>{i18n("nav.main.projects")}</a>,
              children: [
                {
                  key: "project:list",
                  label: <a href={setBaseUrl(`/projects`)}>{i18n("nav.main.project-list")}</a>
                },
                isAdmin && {
                  key: "project:all",
                  label: <a href={setBaseUrl(`/projects/all`)}>{i18n("nav.main.project-list-all")}</a>
                },
                { type: 'divider' },
                {
                  key: "project:sync",
                  label: <a href={setBaseUrl("/projects/synchronize")}>{i18n("nav.main.project-sync")}</a>
                }
              ].filter(Boolean)
            },
            {
              key: "analysis",
              label: <a href={setBaseUrl(`/analysis`)}>{i18n("nav.main.analysis")}</a>,
              children: [
                {
                  key: "analyses:user",
                  label: <a href={setBaseUrl(`/analysis`)}>{i18n("nav.main.analysis-admin-user")}</a>
                },
                isAdmin && {
                  key: "analyses:all",
                  label: <a href={setBaseUrl("/analysis/all")}>{i18n("nav.main.analysis-admin-all")}</a>
                },
                { type: 'divider' },
                {
                  key: "analyses:output",
                  label: <a href={setBaseUrl("/analysis/user/analysis-outputs")}>{i18n("Analysis.outputFiles")}</a>
                }
              ].filter(Boolean)
            },
            !isAdmin && isManager && {
              key: "users",
              label: <a href={setBaseUrl("/users")}>{i18n("nav.main.users")}</a>,
              children: [
                {
                  key: "user:users",
                  label: <a href={setBaseUrl("/users")}>{i18n("nav.main.users-list")}</a>
                },
                {
                  key: "user:groups",
                  label: <a href={setBaseUrl("/groups")}>{i18n("nav.main.groups-list")}</a>
                }
              ]
            },
            isTechnician && {
              key: "sequencing-runs",
              label: <Button type="link" href={setBaseUrl("/sequencing-runs")}>{i18n("nav.main.sequencing-runs")}</Button>
            },
            !isAdmin && {
              key: "remote_api",
              label: <Button type="link" href={setBaseUrl("/remote_api")}>{i18n("nav.main.remoteapis")}</Button>
            }
          ].filter(Boolean)}
        />
      )}

      <div style={{ content: "", flexGrow: 1 }} />
      <GlobalSearch />
      {isAdmin && (
        <div style={{ padding: `0 ${SPACE_MD}` }}>
          <Button
            type="primary"
            className="t-admin-panel-btn"
            href={setBaseUrl("/admin")}
          >
            {i18n("MainNavigation.admin").toUpperCase()}
          </Button>
        </div>
      )}
      <CartLink />
      <AnnouncementsSubMenu />
      <Menu theme={theme} mode="horizontal" defaultSelectedKeys={[""]} items={[
        {
          key: "account-dropdown-link",
          icon: <IconUser />,
          children: [
            {
              key: "account",
              label: <a href={setBaseUrl(`/users/current`)}>{i18n("nav.main.account")}</a>
            },
            {
              key: "help",
              label: i18n("nav.main.help"),
              children: [
                {
                  key: "userguide",
                  label: (
                    <a
                      href="https://phac-nml.github.io/irida-documentation/user/user/"
                      target="_blank"
                      rel="noreferrer"
                    >
                      {i18n("nav.main.userguide")}
                    </a>
                  )
                },
                isAdmin && {
                  key: "adminguide",
                  label: (
                    <a
                      href="https://phac-nml.github.io/irida-documentation/user/administrator/"
                      target="_blank"
                      rel="noreferrer"
                    >
                      {i18n("nav.main.adminguide")}
                    </a>
                  )
                },
                { type: 'divider' },
                {
                  key: "website",
                  label: (
                    <a
                      href="http://www.irida.ca"
                      target="_blank"
                      rel="noopener noreferrer"
                    >
                      {i18n("generic.irida.website")}
                    </a>
                  )
                },
                { type: 'divider' },
                {
                  key: "help:version",
                  label: i18n("irida.version"),
                  disabled: true
                }
              ].filter(Boolean)
            },
            {
              key: "logout",
              label: <a href={setBaseUrl("/logout")}>{i18n("nav.main.logout")}</a>
            }
          ]
        }
      ]} />
    </Header>
  );
}
