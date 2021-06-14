import { Router } from "@reach/router";
import { Layout, Menu, PageHeader } from "antd";
import React, { useState } from "react";
import { render } from "react-dom";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconFolder } from "../icons/Icons";
import { RemoteProjectStatus } from "./RemoteProjectStatus";
const { Item } = Menu;
const { Content } = Layout;
const { SubMenu } = Menu;
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
  const pathTokens = current.split("/");
  const subMenu = pathTokens[pathTokens.length - 1];
  return (
    <PageHeader
      title={<span className="t-project-name">{window.project.label}</span>}
      avatar={{ icon: <IconFolder /> }}
      tags={[<RemoteProjectStatus key="remote" />].filter((f) => f !== null)}
    >
      <Content>
        <Menu mode="horizontal" selectedKeys={[current, subMenu]}>
          <Item key="samples">
            <a href={`${BASE_URL}/samples`}>{i18n("project.nav.samples")}</a>
          </Item>
          <Item key="linelist">
            <a href={`${BASE_URL}/linelist`}>{i18n("project.nav.linelist")}</a>
          </Item>
          <SubMenu key="analyses-submenu" title={i18n("project.nav.analysis")}>
            <Item key="project-analyses">
              <a href={`${BASE_URL}/analyses/project-analyses`}>
                {i18n("project.nav.analyses.project-analyses")}
              </a>
            </Item>
            <Item key="shared-outputs">
              <a href={`${BASE_URL}/analyses/shared-outputs`}>
                {i18n("project.nav.analyses.shared-analyses")}
              </a>
            </Item>
            <Item key="automated-outputs">
              <a href={`${BASE_URL}/analyses/automated-outputs`}>
                {i18n("project.nav.analyses.automated-analyses")}
              </a>
            </Item>
          </SubMenu>
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
