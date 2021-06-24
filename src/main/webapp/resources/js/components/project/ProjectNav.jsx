import { Router } from "@reach/router";
import { Layout, Menu, PageHeader } from "antd";
import React from "react";
import { render } from "react-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconFolder } from "../icons/Icons";
import { RemoteProjectStatus } from "./RemoteProjectStatus";

const { Item } = Menu;
const { Content } = Layout;

/**
 * React component to render a navigation menu for projects.
 * @returns {*}
 * @constructor
 */
export function ProjectNav({ projectId, uri: BASE_URL, ...props }) {
  /*
  Get the current page from the global project object
   */
  const current = props["*"] || "samples";

  return (
    <PageHeader
      title={<span className="t-project-name">{window.project.label}</span>}
      avatar={{ icon: <IconFolder /> }}
      tags={[<RemoteProjectStatus key="remote" />].filter((f) => f !== null)}
    >
      <Content>
        <Menu mode="horizontal" selectedKeys={[current]}>
          <Item key="samples">
            <a href={`${BASE_URL}/samples`}>{i18n("project.nav.samples")}</a>
          </Item>
          <Item key="linelist">
            <a href={`${BASE_URL}/linelist`}>{i18n("project.nav.linelist")}</a>
          </Item>
          <Item key="analyses">
            <a href={`${BASE_URL}/analyses/project-analyses`}>
              {i18n("project.nav.analysis")}
            </a>
          </Item>
          <Item key="export">
            <a href={`${BASE_URL}/export`}>{i18n("project.nav.exports")}</a>
          </Item>
          <Item key="events">
            <a href={`${BASE_URL}/activity`}>{i18n("project.nav.activity")}</a>
          </Item>
          <Item key="settings">
            <a href={`${BASE_URL}/settings/details`}>
              {i18n("project.nav.settings")}
            </a>
          </Item>
        </Menu>
      </Content>
    </PageHeader>
  );
}
render(
  <Router>
    <ProjectNav path={setBaseUrl("/projects/:projectId/**")} />
  </Router>,
  document.querySelector("#project-root")
);
