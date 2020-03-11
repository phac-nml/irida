import React from "react";
import { Input, Menu } from "antd";
import { IconFolder } from "../icons/Icons";
import { setBaseUrl } from "../../utilities/url-utilities";
import { grey1 } from "../../styles/colors";

const { Item, ItemGroup, SubMenu } = Menu;

export function MainNavigation({}) {
  return (
    <div
      style={{
        display: "flex",
        alignItems: "center",
        height: 47,
        justifyContent: "space-between",
        backgroundColor: grey1,
        borderBottom: `1px solid rgb(240, 240, 240)`
      }}
    >
      <Menu mode="horizontal">
        <Item>
          <a href={setBaseUrl("")}>
            <img
              style={{ height: 30 }}
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
      <Input.Search style={{ width: 200 }} />
      <Menu mode="horizontal">
        <Item>
          <a href="/logout">LOGOUT</a>
        </Item>
      </Menu>
    </div>
  );
}
