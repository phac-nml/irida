import { DownloadOutlined } from "@ant-design/icons";
import { Button, Dropdown, Menu } from "antd";
import React from "react";
import { PhyloCanvas } from "../../../../types/phylocanvas";
import { downloadObjectURL } from "../../../../utilities/file-utilities";

interface DownloadMenuProps {
  treeRef: React.MutableRefObject<null | PhyloCanvas>;
}

/**
 * React component to render a drop-down menu allowing the user to download
 * an SVG version of the phylogenetic tree.
 * @param treeRef - react ref to the PhyloCanvas instance.
 * @constructor
 */
export function DownloadMenu({ treeRef }: DownloadMenuProps) {
  const downloadNewick = () => {
    if (treeRef.current) {
      const blob = new Blob([treeRef.current.exportNewick()], {
        type: "text/plain",
      });
      const url = window.URL.createObjectURL(blob);
      downloadObjectURL(url, `tree.newick`);
    }
  };

  const downloadSVG = () => {
    if (treeRef.current) {
      const blob = treeRef.current.exportSVG();
      const url = window.URL.createObjectURL(blob);
      downloadObjectURL(url, `tree.svg`);
    }
  };

  const downloadPNG = () => {
    if (treeRef.current) {
      const url = treeRef.current.exportPNG();
      downloadObjectURL(url, `tree.png`);
    }
  };

  const menu = (
    <Menu>
      <Menu.Item key="newick" onClick={downloadNewick}>
        {i18n("visualization.phylogenomics.button.download.newick")}
      </Menu.Item>
      <Menu.Item key="png" onClick={downloadPNG}>
        {i18n("visualization.phylogenomics.button.download.png")}
      </Menu.Item>
      <Menu.Item key="svg" onClick={downloadSVG}>
        {i18n("visualization.phylogenomics.button.download.svg")}
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown overlay={menu} placement="bottomRight" trigger={["click"]}>
      <Button shape="circle" icon={<DownloadOutlined />} />
    </Dropdown>
  );
}
