import React from "react";
import { MenuItem } from "../../types/ant-design";
import { Menu } from "antd";

const { Item, Divider, SubMenu, ItemGroup } = Menu;

export function renderMenuItem(item: MenuItem): JSX.Element {
  if (item.type === `divider`) {
    return <Divider key={item.key} />;
  } else if (item.type === "group") {
    return (
      <ItemGroup key={item.key} title={item.label}>
        {item.children?.map(renderMenuItem)}
      </ItemGroup>
    );
  } else if (!item.children) {
    return (
      <Item key={item.key} disabled={item.disabled}>
        {item.label}
      </Item>
    );
  } else {
    return (
      <SubMenu key={item.key} title={item.label}>
        {item.children.map(renderMenuItem)}
      </SubMenu>
    );
  }
}
