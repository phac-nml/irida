import { MenuOutlined } from "@ant-design/icons";
import { Button, Dropdown, Menu } from "antd";
import React from "react";
import styled from "styled-components";

const DownloadMenuItem = styled(Menu.Item)`
  .ant-dropdown-menu-title-content {
    width: 100%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
  }
`;

export function LegendDownloadMenu({ terms, onItemClick }) {
  const menu = (
    <Menu style={{ width: 250 }}>
      <Menu.ItemGroup title="Download as SVG"></Menu.ItemGroup>
      {terms.map((term) => {
        const title = i18n(
          "visualization.phylogenomics.sidebar.legend.colour-by",
          term
        );
        return (
          <DownloadMenuItem
            key={term}
            title={title}
            onClick={() => onItemClick(term, "svg")}
          >
            {title}
          </DownloadMenuItem>
        );
      })}
    </Menu>
  );

  return (
    <Dropdown overlay={menu} placement="bottomRight" trigger="click">
      <Button shape="circle" icon={<MenuOutlined />} />
    </Dropdown>
  );
}
