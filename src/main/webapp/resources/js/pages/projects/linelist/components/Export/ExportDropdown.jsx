import React from "react";
import { Button, Dropdown, Menu } from "antd";
import { SPACE_XS } from "../../../../../styles/spacing";
import { IconDropDown } from "../../../../../components/icons/Icons";

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
      <Menu.Item key="excel">{i18n("linelist.toolbar.exportExcel")}</Menu.Item>
      <Menu.Item key="csv">{i18n("linelist.toolbar.exportCsv")}</Menu.Item>
    </Menu>
  );
  return (
    <Dropdown overlay={menu}>
      <Button tour="tour-export">
        {i18n("linelist.toolbar.export")}
        <IconDropDown style={{ marginLeft: SPACE_XS }} />
      </Button>
    </Dropdown>
  );
}