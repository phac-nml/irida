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

  const items = [
    {
      key: "excel",
      label: i18n("linelist.toolbar.exportExcel"),
    },
    {
      key: "csv",
      label: i18n("linelist.toolbar.exportCsv"),
    },
  ];

  return (
    <Dropdown menu={{items}}>
      <Button tour="tour-export">
        {i18n("linelist.toolbar.export")}
        <IconDropDown style={{ marginLeft: SPACE_XS }} />
      </Button>
    </Dropdown>
  );
}
