import { MenuOutlined } from "@ant-design/icons";
import { Button, Dropdown, Menu } from "antd";
import React from "react";

export function DownloadMenu({ treeRef }) {
  const downloadUrl = (url, name) => {
    const link = document.createElement("a");
    link.style.display = "none";
    link.href = url;
    link.setAttribute("download", name);
    document.body.appendChild(link);
    link.click();
    window.URL.revokeObjectURL(url);
  };

  const downloadNewick = () => {
    const blob = new Blob([treeRef.current.exportNewick()], {
      type: "text/plain",
    });
    const url = window.URL.createObjectURL(blob);
    downloadUrl(url, `tree.newick`);
  };

  const downloadSVG = () => {
    const blob = treeRef.current.exportSVG();
    const url = window.URL.createObjectURL(blob);
    downloadUrl(url, `tree.svg`);
  };

  const downloadPNG = () => {
    const url = treeRef.current.exportPNG();
    downloadUrl(url, `tree.png`);
  };

  const menu = (
    <Menu>
      <Menu.Item key="1" onClick={downloadNewick}>
        {i18n("visualization.phylogenomics.button.download.newick")}
      </Menu.Item>
      <Menu.Item key="2" onClick={downloadSVG}>
        {i18n("visualization.phylogenomics.button.download.svg")}
      </Menu.Item>
      <Menu.Item key="3" onClick={downloadPNG}>
        {i18n("visualization.phylogenomics.button.download.png")}
      </Menu.Item>
    </Menu>
  );

  return (
    <Dropdown overlay={menu} placement="bottomRight" trigger="click">
      <Button shape="circle" icon={<MenuOutlined />} />
    </Dropdown>
  );
}
