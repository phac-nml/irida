import React from "react";
import { Input, Menu } from "antd";
import { IconFolder } from "../icons/Icons";
import { setBaseUrl } from "../../utilities/url-utilities";
import { grey1 } from "../../styles/colors";
import { SPACE_MD } from "../../styles/spacing";

const { Item, ItemGroup, SubMenu } = Menu;

export function MainNavigation({}) {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        height: 47,
        backgroundColor: grey1,
        borderBottom: `1px solid rgb(240, 240, 240)`,
      }}
    >
      <a href={setBaseUrl("")}>
        <img
          style={{ height: 30, padding: `0 ${SPACE_MD}` }}
          src={setBaseUrl("/resources/img/irida_logo_light.svg")}
        />
      </a>
      <Input.Search style={{ width: 400 }} />
      <Menu mode="horizontal" style={{ flexGrow: 1 }}>
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
          {window.TL._aa ? (
            <ItemGroup title="Admin">
              <Item key={"admin:projects"}>
                <a href={setBaseUrl(`/projects/all`)}>
                  {i18n("nav.main.project-list-all")}
                </a>
              </Item>
            </ItemGroup>
          ) : null}
        </SubMenu>
      </Menu>
      <Menu mode="horizontal">
        <Item>
          <a href="/logout">LOGOUT</a>
        </Item>
      </Menu>
    </div>
  );
}
