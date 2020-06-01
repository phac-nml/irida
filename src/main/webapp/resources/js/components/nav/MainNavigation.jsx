import React from "react";
import { render } from "react-dom";
import { Menu } from "antd";
import { IconFolder } from "../icons/Icons";
import { setBaseUrl } from "../../utilities/url-utilities";

const { Item, ItemGroup, SubMenu } = Menu;

export function MainNavigation() {
  return (
    <div>
      <Menu theme="dark">
        <Item>
          <a href={setBaseUrl("")}>
            <img
              alt="IRIDA Logo - link home"
              src={setBaseUrl("/resources/img/irida_logo_dark.svg")}
            />
          </a>
        </Item>
        <SubMenu
          title={
            <>
              <IconFolder />
              {i18n("nav.main.project")}
            </>
          }
        >
          <Item key="user:projects">
            <a href={setBaseUrl(`/projects`)}>
              {i18n("nav.main.project-list")}
            </a>
          </Item>
          <Item key="user:connect">
            <a href={setBaseUrl(`/projects/synchronize`)}>
              {i18n("nav.main.project-sync")}
            </a>
          </Item>
          {window.TL._USER.systemRole === "ROLE_ADMIN" ? (
            <ItemGroup title="Admin">
              <Item key={"admin:projects"}>
                <a href={setBaseUrl(`/projects/all`)}>
                  {i18n("nav.main.project-list-all")}
                </a>
              </Item>
            </ItemGroup>
          ) : null}
        </SubMenu>
        <SubMenu
          title={
            <>
              <IconFolder /> {i18n("nav.main.analysis")}
            </>
          }
        >
          <Item key="user:analysis">
            <a href={setBaseUrl(`/analysis`)}>
              {i18n("nav.main.analysis-admin-user")}
            </a>
          </Item>
          {window.TL._USER.systemRole === "ROLE_ADMIN" ? (
            <ItemGroup title="Admin">
              <Item key={"admin:analyses"}>
                <a href={setBaseUrl(`/analysis/all`)}>
                  {i18n("nav.main.analysis-admin-all")}
                </a>
              </Item>
            </ItemGroup>
          ) : null}
          <Menu.Divider />
          <Item key="analysis:outputs">
            <a href={setBaseUrl(`/analysis/user/analysis-outputs`)}>
              {i18n("Analysis.outputFiles")}
            </a>
          </Item>
        </SubMenu>
      </Menu>
    </div>
  );
}

render(<MainNavigation />, document.querySelector("#nav-root"));
