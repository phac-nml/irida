import React from "react";
import { AutoComplete, Input, Menu } from "antd";
import { IconFolder } from "../icons/Icons";
import { setBaseUrl } from "../../utilities/url-utilities";

const { Divider, Item, ItemGroup, SubMenu } = Menu;

export function MainNavigation({}) {
  return (
    <div
      style={{
        backgroundColor: "rgb(5,21,41)",
        display: "flex",
        flexDirection: "row",
        alignContent: "center",
        justifyContent: "space-between"
      }}
    >
      <Menu mode="horizontal" theme={"dark"}>
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
      <AutoComplete>
        <Input.Search />
      </AutoComplete>
    </div>
  );
}
