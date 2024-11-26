import { Layout, Menu, PageHeader } from "antd";
import React from "react";  
import { createRoot } from 'react-dom/client';
import { getProjectIdFromUrl, setBaseUrl } from "../../utilities/url-utilities";
import { IconFolder } from "../icons/Icons";
import { RemoteProjectStatus } from "./RemoteProjectStatus";

const { Item } = Menu;
const { Content } = Layout;

/**
 * React component to render a navigation menu for projects.
 * @returns {*}
 * @constructor
 */
export function ProjectNav({ ...props }) {
  const [current] = React.useState(() => {
    const keyRegex = /\/projects\/\d+\/(?<path>[\w_-]+)/;
    const found = location.pathname.match(keyRegex);
    if (found) {
      return found.groups.path;
    }
    return "samples";
  });
  const projectId = getProjectIdFromUrl();
  const BASE_URL = setBaseUrl(`/projects/${projectId}`);

  return (
    <PageHeader
      title={<span className="t-project-name">{window.project.label}</span>}
      avatar={{ icon: <IconFolder /> }}
      tags={[<RemoteProjectStatus key="remote" />].filter((f) => f !== null)}
    >
      <Content>
        <Menu mode="horizontal" selectedKeys={[current]} items={[
          {
            key: "samples",
            label: <a href={`${BASE_URL}/samples`}>{i18n("project.nav.samples")}</a>
          },
          {
            key: "linelist",
            label: <a href={`${BASE_URL}/linelist`}>{i18n("project.nav.linelist")}</a>
          },
          {
            key: "analyses",
            label: <a href={`${BASE_URL}/analyses/project-analyses`}>{i18n("project.nav.analysis")}</a>
          },
          {
            key: "export",
            label: <a href={`${BASE_URL}/export`}>{i18n("project.nav.exports")}</a>
          },
          {
            key: "activity",
            label: <a href={`${BASE_URL}/activity`}>{i18n("project.nav.activity")}</a>
          },
          {
            key: "settings",
            label: <a href={`${BASE_URL}/settings`}>{i18n("project.nav.settings")}</a>
          }
        ]} />
      </Content>
    </PageHeader>
  );
}

const container = document.querySelector("#project-root");
const root = createRoot(container);
root.render(<ProjectNav />);
