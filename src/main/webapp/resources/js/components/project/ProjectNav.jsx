import { Layout, Menu, PageHeader } from "antd";
import React from "react";
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
export function ProjectNav() {
  /*
  Get the current page from the global project object
   */
  const [current, setCurrent] = React.useState(() => window.project.page);
  const BASE_URL = setBaseUrl(`projects/${window.project.id}/`);

  const pathMatch = window.location.pathname.match(
    /^\/projects\/([0-9]+)\/(\w+)\/[\w+-]+$/
  );
  const pathTokens = pathMatch ? pathMatch[0].split("/") : "";
  const [subMenuKey, setSubMenuKey] = React.useState(
    pathTokens[pathTokens.length - 1]
  );

  return (
    <PageHeader
      title={<span className="t-project-name">{window.project.label}</span>}
      avatar={{ icon: <IconFolder /> }}
      tags={[<RemoteProjectStatus key="remote" />].filter((f) => f !== null)}
    >
      <Content>
        <Menu mode="horizontal" selectedKeys={[current, subMenuKey]}>
          <Item key="samples">
            <a href={`${BASE_URL}samples`}>{i18n("project.nav.samples")}</a>
          </Item>
          <Item key="linelist">
            <a href={`${BASE_URL}linelist`}>{i18n("project.nav.linelist")}</a>
          </Item>
          <SubMenu key="analyses-submenu" title="Analyses">
            <Item key="project-analyses">
              <a href={`${BASE_URL}analyses/project-analyses`}>
                Project Analyses
              </a>
            </Item>
            <Item key="shared-outputs">
              <a href={`${BASE_URL}analyses/shared-outputs`}>
                Shared Single Sample Analysis Outputs
              </a>
            </Item>
            <Item key="automated-outputs">
              <a href={`${BASE_URL}analyses/automated-outputs`}>
                Automated Single Sample Analysis Outputs
              </a>
            </Item>
          </SubMenu>
          <Item key="export">
            <a href={`${BASE_URL}export`}>{i18n("project.nav.exports")}</a>
          </Item>
          <Item key="events">
            <a href={`${BASE_URL}activity`}>{i18n("project.nav.activity")}</a>
          </Item>
          <Item key="settings">
            <a href={`${BASE_URL}settings/details`}>
              {i18n("project.nav.settings")}
            </a>
          </Item>
        </Menu>
      </Content>
    </PageHeader>
  );
}

render(<ProjectNav />, document.querySelector("#project-root"));
