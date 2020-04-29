import React, { useState } from "react";
import { render } from "react-dom";
import { Layout, Menu, PageHeader } from "antd";
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
export function ProjectNav() {
  /*
  Get the current page from the global project object
   */
  const [current, setCurrent] = useState(window.project.page);
  const BASE_URL = setBaseUrl(`projects/${window.project.id}/`);

  return (
    <PageHeader
      title={window.project.label}
      avatar={{ icon: <IconFolder /> }}
      tags={[<RemoteProjectStatus key="remote" />].filter((f) => f !== null)}
    >
      <Content>
        <Menu mode="horizontal" selectedKeys={[current]}>
          <Item key="samples">
            <a href={`${BASE_URL}samples`}>{i18n("project.nav.samples")}</a>
          </Item>
          <Item key="linelist">
            <a href={`${BASE_URL}linelist`}>{i18n("project.nav.linelist")}</a>
          </Item>
          <Item key="analyses">
            <a href={`${BASE_URL}analyses`}>{i18n("project.nav.analysis")}</a>
          </Item>
          <Item key="export">
            <a href={`${BASE_URL}export`}>{i18n("project.nav.exports")}</a>
          </Item>
          <Item key="activity">
            <a href={`${BASE_URL}activity`}>{i18n("project.nav.activity")}</a>
          </Item>
          <Item key="settings">
            <a href={`${BASE_URL}settings`}>{i18n("project.nav.settings")}</a>
          </Item>
        </Menu>
      </Content>
    </PageHeader>
  );
}

render(<ProjectNav />, document.querySelector("#project-root"));
