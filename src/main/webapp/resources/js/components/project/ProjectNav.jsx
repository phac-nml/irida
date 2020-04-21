import React, { useState } from "react";
import { render } from "react-dom";
import { Menu, Layout, PageHeader, Tag } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { SPACE_MD } from "../../styles/spacing";
import { IconFolder } from "../icons/Icons";

const { Item } = Menu;
const { Content } = Layout;

export function ProjectNav() {
  /*
  Get the current page from the global project object
   */
  const [current, setCurrent] = useState(window.project.page);

  return (
    <PageHeader
      title={window.project.label}
      avatar={{ icon: <IconFolder /> }}
      subTitle={`ID: ${window.project.id}`}
      tags={window.project.remote ? <Tag color="blue">REMOTE</Tag> : null}
    >
      <Content>
        <Menu mode="horizontal" selectedKeys={[current]}>
          <Item key="samples">
            <a href={setBaseUrl(`projects/${window.project.id}/samples`)}>
              {i18n("project.nav.samples")}
            </a>
          </Item>
          <Item key="linelist">
            <a href={setBaseUrl(`projects/${window.project.id}/linelist`)}>
              {i18n("project.nav.linelist")}
            </a>
          </Item>
          <Item key="analyses">
            <a href={setBaseUrl(`projects/${window.project.id}/analyses`)}>
              {i18n("project.nav.analysis")}
            </a>
          </Item>
          <Item key="export">
            <a href={setBaseUrl(`projects/${window.project.id}/export`)}>
              {i18n("project.nav.exports")}
            </a>
          </Item>
          <Item key="activity">
            <a href={setBaseUrl(`projects/${window.project.id}/activity`)}>
              {i18n("project.nav.activity")}
            </a>
          </Item>
          <Item key="settings">
            <a href={setBaseUrl(`projects/${window.project.id}/settings`)}>
              {i18n("project.nav.settings")}
            </a>
          </Item>
        </Menu>
      </Content>
    </PageHeader>
  );
}

render(<ProjectNav />, document.querySelector("#project-root"));
