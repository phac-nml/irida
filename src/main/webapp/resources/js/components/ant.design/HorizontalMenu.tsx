import React from "react";
import type { MenuProps } from "antd";
import { Menu } from "antd";

const HorizontalMenu: React.FC<Partial<MenuProps>> = (props) => (
  <Menu
    style={{
      display: "flex",
      justifyContent: "flex-start",
      borderBottom: `1px solid var(--grey-4)`,
    }}
    {...props}
  />
);

export default HorizontalMenu;
