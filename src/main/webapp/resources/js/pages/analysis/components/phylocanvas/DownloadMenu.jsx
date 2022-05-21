import { MenuOutlined } from "@ant-design/icons";
import { Button, Popover, Space } from "antd";
import React from "react";

export function DownloadMenu({treeRef}) {

  const downloadBlob = (blob, name) => {
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.style.display = "none";
    link.href = url;
    link.setAttribute("download", name);
    document.body.appendChild(link);
    link.click();
    window.URL.revokeObjectURL(url);
  }

  const downloadNewick = () => {
    const blob = treeRef.current.exportNewick();
    downloadBlob(blob, `tree.newick`)
  };

  const downloadSVG = () => {
    const blob = treeRef.current.exportSVG();
    downloadBlob(blob, `tree.svg`)
  };

  const downloadPNG = () => {
    const blob = treeRef.current.exportPNG();
    downloadBlob(blob, `tree.png`)
  };

  return (
    <Popover
      content={
        <Space direction="vertical">
          <Button onClick={downloadNewick}>
            {i18n("visualization.phylogenomics.button.download.newick")}
          </Button>
          <Button onClick={downloadSVG}>
            {i18n("visualization.phylogenomics.button.download.svg")}
          </Button>
          <Button onClick={downloadPNG}>
            {i18n("visualization.phylogenomics.button.download.png")}
          </Button>
        </Space>
      }
      placement="bottomRight"
    >
      <Button shape="circle" icon={<MenuOutlined />} />
    </Popover>
  );
}