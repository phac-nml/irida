import React from "react";
import { Dropdown, DropDownProps } from "antd";
import { MoreOutlined } from "@ant-design/icons";

/**
 * React component to display an ellipsis menu
 * @param menu The dropdown menu
 * @param className The class name
 * @param placement Where to position dropdown
 * @param trigger How the dropdown is activated
 * @param props Any other props provided for the dropdown
 * @returns {JSX.Element}
 * @constructor
 */
export function EllipsisMenu({
  overlay,
  className = "t-actions-menu",
  placement = "bottomRight",
  trigger = ["click"],
  ...props
}: DropDownProps): JSX.Element {
  return (
    <Dropdown
      overlay={overlay}
      className={className}
      trigger={trigger}
      placement={placement}
      {...props}
    >
      <MoreOutlined />
    </Dropdown>
  );
}
