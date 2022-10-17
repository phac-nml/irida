import { Layout, Menu, PageHeader } from "antd";
import type { MenuProps } from "antd";
import React from "react";
import { createRoot } from "react-dom/client";
import { getProjectIdFromUrl, setBaseUrl } from "../../utilities/url-utilities";
import { IconFolder } from "../icons/Icons";
import { RemoteProjectStatus } from "./RemoteProjectStatus";

declare let window: IridaWindow;

const { Content } = Layout;

const menuItems: MenuProps["items"] = [
  {
    key: "project:samples",
    label: i18n("project.nav.samples"),
  },
  {
    key: "project:linelist",
    label: i18n("project.nav.linelist"),
  },
  {
    key: "project:analyses",
    label: i18n("project.nav.analysis"),
  },
  {
    key: "project:exports",
    label: i18n("project.nav.exports"),
  },
  {
    key: "project:activity",
    label: i18n("project.nav.activity"),
  },
  {
    key: "project:settings",
    label: i18n("project.nav.settings"),
  },
];

/**
 * React component to render a navigation menu for projects.
 */
export function ProjectNav(): JSX.Element {
  const [current] = React.useState(() => {
    const keyRegex = /\/projects\/\d+\/(?<path>[\w+_-]+)/;
    const found = location.pathname.match(keyRegex);
    if (found && found.groups) {
      return `project:${found.groups.path}`;
    }
    return "project:samples";
  });
  const projectId = getProjectIdFromUrl();
  const BASE_URL = setBaseUrl(`/projects/${projectId}`);

  const onClick: MenuProps["onClick"] = ({ key }) => {
    switch (key) {
      case "project:samples":
        window.location.href = `${BASE_URL}/samples`;
        break;
      case "project:linelist":
        window.location.href = `${BASE_URL}/linelist`;
        break;
      case "project:analyses":
        window.location.href = `${BASE_URL}/analyses/project-analyses`;
        break;
      case "project:exports":
        window.location.href = `${BASE_URL}/ncbi`;
        break;
      case "project:activity":
        window.location.href = `${BASE_URL}/activity`;
        break;
      case "project:settings":
        window.location.href = `${BASE_URL}/settings`;
        break;
    }
  };

  return (
    <PageHeader
      title={<span className="t-project-name">{window.project.label}</span>}
      avatar={{ icon: <IconFolder /> }}
      tags={[<RemoteProjectStatus key="remote" />].filter((f) => f !== null)}
    >
      <Content>
        <Menu
          items={menuItems}
          mode="horizontal"
          onClick={onClick}
          selectedKeys={[current]}
        />
      </Content>
    </PageHeader>
  );
}

const element = document.querySelector("#project-root");
if (element) {
  const root = createRoot(element);
  root.render(<ProjectNav />);
} else {
  throw new Error("Cannot find DOM element #project-root");
}
