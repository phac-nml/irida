import React from "react";
import PropTypes from "prop-types";
import { Button, Dropdown, Menu } from "antd";

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
      <Menu.Item key="excel">{__("linelist.toolbar.exportExcel")}</Menu.Item>
      <Menu.Item key="csv">{__("linelist.toolbar.exportCsv")}</Menu.Item>
    </Menu>
  );
  return (
    <Dropdown overlay={menu}>
      <Button tour="tour-export">
        {__("linelist.toolbar.export")}
        <i
          className="fas fa-chevron-down spaced-left__sm"
          data-fa-transform="shrink-4"
        />
      </Button>
    </Dropdown>
  );
}

ExportDropDown.propTypes = {
  csv: PropTypes.func.isRequired
};
