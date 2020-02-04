import React from "react";
import { Layout, Menu } from "antd";
import { FolderOutlined } from "@ant-design/icons";

const { Item, ItemGroup, SubMenu } = Menu;
const { Header } = Layout;

export function MainNavigation({}) {
  return (
    <Menu theme="dark" mode="horizontal" style={{ lineHeight: "64px" }}>
      <SubMenu
        title={
          <>
            <FolderOutlined />
            {i18n("nav.main.project")}
          </>
        }
      >
        <Item key="user:projects">{i18n("nav.main.project-list")}</Item>
        <Item key={"admin:projects"}>{i18n("nav.main.project-list-all")}</Item>
        <ItemGroup title="Item 1">
          <Item key="setting:1">Option 1</Item>
          <Item key="setting:2">Option 2</Item>
        </ItemGroup>
      </SubMenu>
    </Menu>
  );
}
