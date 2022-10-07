import { DownloadOutlined } from "@ant-design/icons";
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

export type HandleLegendSectionDownload = (term: string) => void;

type LegendDownloadMenuProps = {
  onItemClick: HandleLegendSectionDownload;
  terms: string[];
};

/**
 * React component to render a menu for download individual sections of the phylocanvas
 * colour menu.
 */
export function LegendDownloadMenu({
  terms,
  onItemClick,
}: LegendDownloadMenuProps) {
  const menu = (
    <Menu style={{ width: 250 }}>
      <Menu.ItemGroup title={i18n("LegendDownloadMenu.title")}></Menu.ItemGroup>
      {terms.map((term) => {
        const title = i18n(
          "visualization.phylogenomics.sidebar.legend.colour-by",
          term
        );
        return (
          <DownloadMenuItem
            key={term}
            title={title}
            onClick={() => onItemClick(term)}
          >
            {title}
          </DownloadMenuItem>
        );
      })}
    </Menu>
  );

  return (
    <Dropdown overlay={menu} placement="bottomRight" trigger={["click"]}>
      <Button shape="circle" icon={<DownloadOutlined />} />
    </Dropdown>
  );
}
