import { MenuOutlined } from "@ant-design/icons";
import { Button, Popover, Space } from "antd";
import React from "react";
import styled from "styled-components";

const DownloadButton = styled(Button)`
  text-align: left;
  width: 100%;
`;

export function DownloadMenu({treeRef}) {

  const downloadUrl = (url, name) => {
    const link = document.createElement("a");
    link.style.display = "none";
    link.href = url;
    link.setAttribute("download", name);
    document.body.appendChild(link);
    link.click();
    window.URL.revokeObjectURL(url);
  }

  const downloadNewick = () => {
    const blob = new Blob([treeRef.current.exportNewick()], {type: 'text/plain'});
    const url = window.URL.createObjectURL(blob);
    downloadUrl(url, `tree.newick`)
  };

  const downloadSVG = () => {
    const blob = treeRef.current.exportSVG();
    const url = window.URL.createObjectURL(blob);
    downloadUrl(url, `tree.svg`)
  };

  const downloadPNG = () => {
    const url = treeRef.current.exportPNG();
    downloadUrl(url, `tree.png`)
  };

  return (
    <Popover
      content={
        <Space direction="vertical">
          <DownloadButton onClick={downloadNewick}>
            {i18n("visualization.phylogenomics.button.download.newick")}
          </DownloadButton>
          <DownloadButton onClick={downloadSVG}>
            {i18n("visualization.phylogenomics.button.download.svg")}
          </DownloadButton>
          <DownloadButton onClick={downloadPNG}>
            {i18n("visualization.phylogenomics.button.download.png")}
          </DownloadButton>
        </Space>
      }
      placement="bottomRight"
    >
      <Button shape="circle" icon={<MenuOutlined />} />
    </Popover>
  );
}