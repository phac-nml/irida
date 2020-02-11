import React from "react";
import PropTypes from "prop-types";
import { Button, Dropdown, Menu } from "antd";
import {
  DownloadOutlined,
  FileExcelOutlined,
  FileOutlined
} from "@ant-design/icons";

export function ExportDropDown(props) {
  const onClick = ({ key }) => {
    if (typeof props[key] !== "undefined") {
      props[key]();
    } else {
      throw new Error(`No export function for key: ${key}`);
    }
  };

  const menu = (
    <Menu onClick={onClick}>
      <Menu.Item key="excel">
        <FileExcelOutlined />
        {i18n("linelist.toolbar.exportExcel")}
      </Menu.Item>
      <Menu.Item key="csv">
        <FileOutlined />
        {i18n("linelist.toolbar.exportCsv")}
      </Menu.Item>
    </Menu>
  );
  return (
    <Dropdown overlay={menu}>
      <Button tour="tour-export">
        {i18n("linelist.toolbar.export")}
        <DownloadOutlined />
      </Button>
    </Dropdown>
  );
}

ExportDropDown.propTypes = {
  csv: PropTypes.func.isRequired
};
